package de.x4fyr.paiman.lib.adapter

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.provider.BaseColumns
import android.util.Log
import com.jakewharton.disklrucache.DiskLruCache
import de.x4fyr.paiman.app.error
import de.x4fyr.paiman.app.writeTo
import de.x4fyr.paiman.lib.SettableFuture
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.TreeMap
import java.util.UUID
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock

/**
 * Asynchronous cache layer for [AndroidGoogleDriveStorageAdapter]
 */
@Singleton
class CachedGoogleDriveStorageAdapter @Inject constructor(context: Context,
                                                          private val androidGoogleDriveStorageAdapter:
                                                          AndroidGoogleDriveStorageAdapter): GoogleDriveStorageAdapter {

    companion object {
        private const val DISK_CACHE_DIR = "images"
        private const val DISK_CACHE_SIZE = 1024*1024*10L
        private const val QUEUE_CACHE_DIR = "queued_images"
        private const val ACTION_THREAD_NAME = "gdrive_sync"
        private const val GET_THREAD_NAME = "gdrive_getter"
    }


    private val cacheDir: String by lazy {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                && !Environment.isExternalStorageRemovable()) context.externalCacheDir.path
        else context.cacheDir.path
    }

    private val queueCacheDir: String by lazy {
        File(cacheDir + File.separator + QUEUE_CACHE_DIR).apply { if (!exists()) mkdirs() }.path

    }

    private val fileCache: DiskLruCache by lazy {
        val cacheDir = File(cacheDir + File.separator + DISK_CACHE_DIR).apply { if (!exists()) mkdirs() }
        DiskLruCache.open(cacheDir, context.packageManager.getPackageInfo(context.packageName, 0).versionCode, 1,
                DISK_CACHE_SIZE)
    }

    private val actionQueueDB = ActionQueueOpenHelper(context).writableDatabase
    private val actionQueueLock = ReentrantLock()
    private val actionCondition = actionQueueLock.newCondition()
    private var currentPosition = run<Long> {
        var result = 0L
        val columns = arrayOf(BaseColumns._ID, ActionQueueEntry
                .COLUMN_NAME_ACTION, ActionQueueEntry.COLUMN_NAME_IMAGE_ID, ActionQueueEntry.COLUMN_NAME_POSITION)
        val sortOrder = ActionQueueEntry.COLUMN_NAME_POSITION + " DESC"
        actionQueueDB.query(
                ActionQueueEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                result = cursor.getLong(cursor.getColumnIndexOrThrow(ActionQueueEntry.COLUMN_NAME_POSITION))
            }
        }
        result
    }

    private val getterQueue = TreeMap<String, SettableFuture<InputStream>>()
    private val getterLock = ReentrantLock()
    private val getterCondition = getterLock.newCondition()

    init {
        launch(newSingleThreadContext(ACTION_THREAD_NAME)) {
            while (true) {
                var resultAction: Action? = null
                actionQueueLock.withLock /*Conditional fetch/wait for actions*/{
                    while (resultAction == null) {
                        val columns = arrayOf(BaseColumns._ID, ActionQueueEntry
                                .COLUMN_NAME_ACTION, ActionQueueEntry.COLUMN_NAME_IMAGE_ID,
                                ActionQueueEntry.COLUMN_NAME_POSITION)
                        val sortOrder = ActionQueueEntry.COLUMN_NAME_POSITION + " ASC"
                        actionQueueDB.query(
                                ActionQueueEntry.TABLE_NAME,
                                columns,
                                null,
                                null,
                                null,
                                null,
                                sortOrder
                        ).use { cursor ->
                            resultAction = if (cursor.moveToFirst()) {
                                Action(id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)),
                                        action = Action.Actions.valueOf(cursor.getString(
                                                cursor.getColumnIndex(ActionQueueEntry.COLUMN_NAME_ACTION))),
                                        imageId = cursor.getString(
                                                cursor.getColumnIndex(ActionQueueEntry.COLUMN_NAME_IMAGE_ID)))

                            } else null
                        }
                        if (resultAction == null) actionCondition.await()
                    }

                }
                val action = resultAction
                if (action != null) {
                    Log.d(ACTION_THREAD_NAME, "Starting new action: ${action.action} on ${action.imageId}")
                    try {
                        when (action.action) {
                            Action.Actions.SAVE -> {
                                val file = File(queueCacheDir + File.separator + action.imageId)
                                androidGoogleDriveStorageAdapter.saveImage(file.inputStream(), id = action.imageId)
                                file.delete()
                            }
                            Action.Actions.DELETE -> {
                                androidGoogleDriveStorageAdapter.deleteImage(action.imageId)
                            }
                        }
                        dequeue(action)
                        Log.d(ACTION_THREAD_NAME, "Ended action: ${action.action} on ${action.imageId}")
                    } catch (e: StorageAdapter.StorageException) {
                        Log.w(ACTION_THREAD_NAME, "StorageError while handling action", e)
                        Thread.sleep(5000)
                    }
                }
            }
        }
        launch(newSingleThreadContext(GET_THREAD_NAME)) {
            while (true) {
                var entry: Map.Entry<String, SettableFuture<InputStream>>? = null
                getterLock.withLock {
                    while (getterQueue.isEmpty()) {
                        getterCondition.await()
                    }
                    entry = getterQueue.firstEntry()!!
                }
                val (id, future) = entry!!
                val resultFuture = androidGoogleDriveStorageAdapter.getImage(id)
                try {
                    val result = resultFuture.get()
                    val editor = fileCache.edit(id)
                    val cacheOutStream = editor.newOutputStream(0)
                    val cacheInStream = editor.newInputStream(0)
                    editor.commit()
                    result.writeTo(cacheOutStream)
                    future.set(cacheInStream)
                    getterLock.withLock { getterQueue.remove(id) }
                } catch (e: ExecutionException) {
                    Log.w(GET_THREAD_NAME, "StorageError while handling action", e.cause)
                    if (e.cause is StorageAdapter.StorageException.EntityDoesNotExist) {
                        getterLock.withLock { getterQueue.remove(id) }
                        val cause = e.cause!!
                        future.except(cause)
                    } else {
                        Thread.sleep(3000)
                    }
                }
            }
        }
    }

    /** Get image as [InputStream] from storage */
    suspend override fun getImage(id: String): Future<InputStream> {
        //First try to get from fileCache
        val cached = fileCache[id]
        return if (cached != null) {
            SettableFuture<InputStream>().apply { set(cached.getInputStream(0)) }
        } else { //Try to find in queue
            val file = File(queueCacheDir + File.separator + id)
            if (file.exists()) {
                val editor = fileCache.edit(id)
                val cacheOutStream = editor.newOutputStream(0)
                editor.commit()
                file.inputStream().writeTo(cacheOutStream)
                SettableFuture<InputStream>().apply { set(file.inputStream()) }
            } else { //Fetch from Google Drive
                getterLock.withLock {
                    getterQueue[id] ?: SettableFuture<InputStream>().also {
                        getterQueue.put(id, it)
                        getterCondition.signal()
                    }
                }
            }
        }
    }

    /** Save image from [InputStream] to Storage */
    suspend override fun saveImage(image: InputStream, id: String?): String {
        val newId: String
        // Generate id
        val markableStream: InputStream = if (image.markSupported()) image
        else image.buffered(image.available())
        markableStream.mark(Int.MAX_VALUE)
        if (id != null) {
            newId = id
        } else {
            newId = UUID.nameUUIDFromBytes(markableStream.readBytes()).toString()
            markableStream.reset()
        }
        val queuedFile = File(queueCacheDir + File.separator + newId)
        // Write file and enqueue
        try {
            queuedFile.createNewFile()
            markableStream.writeTo(queuedFile.outputStream())
            enqueue(Action(action = Action.Actions.SAVE, imageId = newId))
        } catch (ioe: IOException) {
            throw error("Image could not be saved.", ioe)
        }
        return newId
    }

    /** Delete image from storage */
    suspend override fun deleteImage(id: String) {
        enqueue(Action(action = Action.Actions.DELETE, imageId = id))
    }

    private fun enqueue(action: Action) {
        val contentValues = ContentValues().apply {
            put(ActionQueueEntry.COLUMN_NAME_POSITION, ++currentPosition)
            put(ActionQueueEntry.COLUMN_NAME_IMAGE_ID, action.imageId)
            put(ActionQueueEntry.COLUMN_NAME_ACTION, action.action.toString())
        }
        actionQueueLock.withLock {
            actionQueueDB.insert(ActionQueueEntry.TABLE_NAME, null, contentValues)
            actionCondition.signal()
        }
    }

    private fun dequeue(action: Action) {
        if (action.id != null) {
            actionQueueLock.withLock {
                actionQueueDB.delete(ActionQueueEntry.TABLE_NAME,
                        "${BaseColumns._ID} = ?",
                        arrayOf(action.id.toString()))
            }
        }
    }
}

private data class Action(val id: Long? = null, val action: Actions, val imageId: String) {
    /** Possible actions that will be handled */
    enum class Actions {
        /** Save image */
        SAVE,
        /** delete image */
        DELETE
    }
}

private class ActionQueueOpenHelper constructor(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     *
     *
     * The SQLite ALTER TABLE documentation can be found
     * [here](http://sqlite.org/lang_altertable.html). If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     *
     *
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     *
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DROP_TABLE)
        onCreate(db)
    }

    /**
     * Called when the database needs to be downgraded. This is strictly similar to
     * [.onUpgrade] method, but is called whenever current version is newer than requested one.
     * However, this method is not abstract, so it is not mandatory for a customer to
     * implement it. If not overridden, default implementation will reject downgrade and
     * throws SQLiteException
     *
     *
     *
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     *
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        private const val DB_NAME = "qdrive_action_queue"
        private const val DB_VERSION = 1
        private val SQL_CREATE_TABLE = """CREATE TABLE ${ActionQueueEntry.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY,
                ${ActionQueueEntry.COLUMN_NAME_POSITION} INTEGER,
                ${ActionQueueEntry.COLUMN_NAME_ACTION} TEXT,
                ${ActionQueueEntry.COLUMN_NAME_IMAGE_ID} TEXT)"""
        private val SQL_DROP_TABLE = " DROP TABLE IF EXISTS ${ActionQueueEntry.TABLE_NAME} "
    }

}

private object ActionQueueEntry: BaseColumns {
    /** Name of sqlite table */
    const val TABLE_NAME = "gdrive_action_queue"
    /** position column name */
    const val COLUMN_NAME_POSITION = "position"
    /** action column name */
    const val COLUMN_NAME_ACTION = "action"
    /** image id column name */
    const val COLUMN_NAME_IMAGE_ID = "image_id"
}

