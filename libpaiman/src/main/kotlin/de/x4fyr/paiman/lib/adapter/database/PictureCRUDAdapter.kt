package de.x4fyr.paiman.lib.adapter.database

import java.io.InputStream

/**
 * Created by x4fyr on 3/22/17.
 */
internal interface PictureCRUDAdapter {

    fun createPicture(pictureStream: InputStream, paintingID: String): String
    fun readPicture(id: String, paintingID: String): InputStream?
    fun readPictures(ids: Set<String>, paintingID: String): Set<InputStream>
    fun updatePicture(id: String, pictureStream: InputStream, paintingID: String)
    fun deletePicture(id: String, paintingID: String)

}