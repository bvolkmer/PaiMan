package de.x4fyr.paiman.lib.services

import com.couchbase.lite.Query
import com.couchbase.lite.QueryEnumerator
import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.SavedPainting

/**
 * Created by x4fyr on 3/22/17.
 */
interface QueryService {
    val allPaintingsQuery: Query
    val allQuery: Query
}
