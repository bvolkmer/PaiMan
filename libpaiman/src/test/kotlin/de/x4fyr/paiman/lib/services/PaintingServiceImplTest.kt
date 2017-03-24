package de.x4fyr.paiman.lib.services

import de.x4fyr.paiman.lib.adapter.database.PaintingCRUDAdapter
import de.x4fyr.paiman.lib.adapter.database.QueryAdapter
import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.Purchaser
import de.x4fyr.paiman.lib.domain.SellingInformation
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.BDDMockito.*
import util.*
import java.time.LocalDate
import java.util.Random
import java.util.stream.Collectors

/**
 * @author de.x4fyr
 * *         Created on 3/4/17.
 */
class PaintingServiceImplTest {

    lateinit var service: PaintingService
    lateinit var paintingCRUDAdapter: PaintingCRUDAdapter
    lateinit var queryAdapter: QueryAdapter
    lateinit var mockPainting: Painting
    lateinit var mockPicture: Picture
    lateinit var mockWip: Set<Picture>
    lateinit var mockRef: Set<Picture>
    lateinit var mockSellInfo: SellingInformation
    lateinit var mockTags: Set<String>
    lateinit var mockDate: LocalDate
    val maxListSize = 100

    companion object {
        val RAND = Random()
        val ID = RAND.nextString()
    }

    @Before
    fun setup() {
        paintingCRUDAdapter = mock(paintingCRUDAdapter::class.java)
        queryAdapter = mock(QueryAdapter::class.java)
        service = MainServiceImpl(paintingCRUDAdapter = paintingCRUDAdapter, queryAdapter = queryAdapter)
        mockPainting = mock(Painting::class.java)
        mockPicture = mock(Picture::class.java)
        mockWip = List(RAND.nextPositiveInt(maxListSize), { mock(Picture::class.java) }).toHashSet()
        mockRef = List(RAND.nextPositiveInt(maxListSize), { mock(Picture::class.java) }).toHashSet()
        mockSellInfo = mock(SellingInformation::class.java)
        mockTags = List(RAND.nextPositiveInt(maxListSize), { RAND.nextString(maxListSize) }).toHashSet()
        mockDate = mock(LocalDate::class.java)
    }

    @Test
    fun composeNewPainting() {
        //given
        val am = ArgumentMatcher<Painting> {
            equalPainting(it, Painting(
                    id = null,
                    title =,
                    wip = mockWip,
                    references = mockRef,
                    finishingDate = null,
                    sellingInfo = mockSellInfo,
                    tags = mockTags, mainPicture = mockPicture))
        }
        given(paintingCRUDAdapter.create(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.composeNewPainting(title =, mainPicture = mockPicture, wip = mockWip,
                reference = mockRef, sellingInfo =
                mockSellInfo, tags = mockTags)
        //then
        then(paintingCRUDAdapter).should(only()).create(argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun replaceMainPictureWithoutMove() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags, mainPicture = mockPicture)
        val newPicture = mock(Picture::class.java)
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(mainPicture = newPicture)) }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.replaceMainPicture(painting = painting, newPicture = newPicture, moveOldToWip = false)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun replaceMainPictureWithMove() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags, mainPicture = mockPicture)
        val newPicture = mock(Picture::class.java)
        val am = ArgumentMatcher<Painting> {
            equalPainting(it, painting.copy(mainPicture = newPicture,
                    wip = painting.wip + painting.mainPicture))
        }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.replaceMainPicture(painting = painting, newPicture = newPicture, moveOldToWip = true)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun sellPainting() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = null, tags = mockTags, mainPicture = mockPicture)
        val mockPurchaser = mock(Purchaser::class.java)
        val mockPDate = mock(LocalDate::class.java)
        val price = RAND.nextPositiveDouble()
        val sellInfo = SellingInformation(purchaser = mockPurchaser, date = mockPDate, price = price)
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(sellingInfo = sellInfo)) }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.sellPainting(painting = painting, purchaser = mockPurchaser, date = mockPDate, price =
        price)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun addWipPicture() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags, mainPicture = mockPicture)
        val newPictures = List<Picture>(RAND.nextPositiveInt(maxListSize), { mock(Picture::class.java) }).toSet()
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(wip = painting.wip + newPictures)) }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.addWipPicture(painting = painting, images = newPictures)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun removeWipPicture() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags, mainPicture = mockPicture)
        val removePictures = mockWip.stream().filter { RAND.nextBoolean() }.collect(Collectors.toSet())
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(wip = painting.wip - removePictures)) }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.removeWipPicture(painting = painting, images = removePictures)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun addReferences() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags, mainPicture = mockPicture)
        val newPictures = List<Picture>(RAND.nextPositiveInt(maxListSize), { mock(Picture::class.java) }).toSet()
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(references = painting.references + newPictures)) }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.addReferences(painting = painting, references = newPictures)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun removeReferences() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags, mainPicture = mockPicture)
        val removePictures = mockRef.stream().filter { RAND.nextBoolean() }.collect(Collectors.toSet())
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(references = painting.references -
                removePictures)) }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.removeReferences(painting = painting, references = removePictures)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun addTags() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags, mainPicture = mockPicture)
        val newTags = List(RAND.nextPositiveInt(maxListSize), { RAND.nextString(maxListSize) }).toSet()
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(tags = painting.tags + newTags)) }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.addTags(painting = painting, tags = newTags)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun removeTags() {
        //given
        val painting = Painting(id = ID, title =, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags, mainPicture = mockPicture)
        val removeTags = mockTags.stream().filter { RAND.nextBoolean() }.collect(Collectors.toSet())
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(tags = painting.tags -
                removeTags)) }
        given(paintingCRUDAdapter.update(, argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.removeTags(painting = painting, tags = removeTags)
        //then
        then(paintingCRUDAdapter).should(only()).update(, argThatNonNull(am))
        then(paintingCRUDAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    private fun equalPainting(given: Painting?, expected: Painting): Boolean = given?.equals(expected) ?: false
}