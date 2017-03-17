package de.x4fyr.paiman.lib.adapter

import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.Purchaser
import kotlinx.coroutines.experimental.Deferred
import java.time.LocalDate
import java.util.concurrent.Future

/**
 * @author de.x4fyr
 * Created on 3/4/17.
 */
interface DatabaseAdapter {

    fun getAll(): Deferred<Set<Painting>>

    fun getById(id: Int): Deferred<Painting>

    fun getByTag(tag: String): Deferred<Set<Painting>>

    fun getByTagsOr(tags: Set<String>): Deferred<Set<Painting>>

    fun getByTagsAnd(tags: Set<String>): Deferred<Set<Painting>>

    fun getByPurchaser(purchaser: Purchaser): Deferred<Set<Painting>>

    fun getByDate(date: LocalDate): Deferred<Set<Painting>>

    fun getByPicture(picture: Picture): Deferred<Painting>

    fun remove(painting: Painting)

    fun createPainting(painting: Painting): Painting

    fun updatePainting(painting: Painting): Painting
}