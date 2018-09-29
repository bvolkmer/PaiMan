package de.x4fyr.paiman.util

/** LRUCache using LinkedHashMap and overriding [removeEldestEntry] */
class LRUCacheMap<K, V>(private val cacheSize: Int): LinkedHashMap<K, V>(cacheSize, 0.75F, true) {


    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size >= cacheSize
    }
}