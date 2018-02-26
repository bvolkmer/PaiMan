package de.x4fyr.paiman.app

/** [Collection.map] like function for single objects
 *
 * to be used with kotlin ?. null handling
 */
fun <T, R> T.transform(transform: (T) -> R): R = transform(this)


/** LRUCache using LinkedHashMap and overriding [removeEldestEntry] */
class LRUCacheMap<K, V>(private val cacheSize: Int): LinkedHashMap<K, V>(cacheSize, 0.75F, true) {


    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size >= cacheSize
    }
}