package de.x4fyr.paiman.lib.services

import com.couchbase.lite.Query

/**
 * Basic service handling couchbase queries
 */
interface QueryService {
    /** A query containing all paintings */
    val allPaintingsQuery: Query
    /** A query containing everything */
    val allQuery: Query
}
