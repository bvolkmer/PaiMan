package de.x4fyr.paiman.lib.adapter

import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.Purchaser
import kotlinx.coroutines.experimental.Deferred
import java.time.LocalDate

/**
 * @author x4fyr
 * Created on 3/17/17.
 */
class DummyDatabaseAdapter : DatabaseAdapter {
    override fun getAll(): Deferred<Set<Painting>> {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun getById(id: Int): Deferred<Painting> {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun getByTag(tag: String): Deferred<Set<Painting>> {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun getByTagsOr(tags: Set<String>): Deferred<Set<Painting>> {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun getByTagsAnd(tags: Set<String>): Deferred<Set<Painting>> {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun getByPurchaser(purchaser: Purchaser): Deferred<Set<Painting>> {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun getByDate(date: LocalDate): Deferred<Set<Painting>> {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun getByPicture(picture: Picture): Deferred<Painting> {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun remove(painting: Painting) {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun createPainting(painting: Painting): Painting {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun updatePainting(painting: Painting): Painting {
        throw UnsupportedOperationException("not implemented") //TODO
    }
}