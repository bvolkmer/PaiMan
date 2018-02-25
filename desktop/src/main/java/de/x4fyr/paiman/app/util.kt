package de.x4fyr.paiman.app

/** [Collection.map] like function for single objects
 *
 * to be used with kotlin ?. null handling
 */
fun <T, R> T.transform(transform: (T) -> R): R = transform(this)
