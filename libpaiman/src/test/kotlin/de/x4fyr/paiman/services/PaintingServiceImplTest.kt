package de.x4fyr.paiman.services

import de.x4fyr.paiman.adapter.DatabaseAdapter
import de.x4fyr.paiman.domain.Painting
import de.x4fyr.paiman.domain.Picture
import de.x4fyr.paiman.domain.Purchaser
import de.x4fyr.paiman.domain.SellingInformation
import de.x4fyr.paiman.domain.dateTime.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.BDDMockito.*
import util.*
import java.util.Random
import java.util.stream.Collectors

/**
 * @author x4fyr
 * *         Created on 3/4/17.
 */
class PaintingServiceImplTest {

    lateinit var service: PaintingService
    lateinit var databaseAdapter: DatabaseAdapter
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
        val ID = RAND.nextPositiveLong()
    }

    @Before
    fun setup() {
        databaseAdapter = mock(DatabaseAdapter::class.java)
        service = PaintingServiceImpl(databaseAdapter = databaseAdapter)
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
                    mainPicture = mockPicture,
                    wip = mockWip,
                    references = mockRef,
                    finishingDate = null,
                    sellingInfo = mockSellInfo,
                    tags = mockTags))
        }
        given(databaseAdapter.createPainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.composeNewPainting(mainPicture = mockPicture, wip = mockWip, reference = mockRef,
                sellingInfo =
                mockSellInfo, tags = mockTags)
        //then
        then(databaseAdapter).should(only()).createPainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun replaceMainPictureWithoutMove() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags)
        val newPicture = mock(Picture::class.java)
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(mainPicture = newPicture)) }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.replaceMainPicture(painting = painting, newPicture = newPicture, moveOldToWip = false)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun replaceMainPictureWithMove() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags)
        val newPicture = mock(Picture::class.java)
        val am = ArgumentMatcher<Painting> {
            equalPainting(it, painting.copy(mainPicture = newPicture,
                    wip = painting.wip + painting.mainPicture))
        }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.replaceMainPicture(painting = painting, newPicture = newPicture, moveOldToWip = true)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun sellPainting() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = null, tags = mockTags)
        val mockPurchaser = mock(Purchaser::class.java)
        val mockPDate = mock(LocalDate::class.java)
        val price = RAND.nextPositiveDouble()
        val sellInfo = SellingInformation(purchaser = mockPurchaser, date = mockPDate, price = price)
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(sellingInfo = sellInfo)) }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.sellPainting(painting = painting, purchaser = mockPurchaser, date = mockPDate, price =
        price)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun addWipPicture() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags)
        val newPictures = List<Picture>(RAND.nextPositiveInt(maxListSize), { mock(Picture::class.java) }).toSet()
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(wip = painting.wip + newPictures)) }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.addWipPicture(painting = painting, images = newPictures)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun removeWipPicture() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags)
        val removePictures = mockWip.stream().filter { RAND.nextBoolean() }.collect(Collectors.toSet())
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(wip = painting.wip - removePictures)) }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.removeWipPicture(painting = painting, images = removePictures)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun addReferences() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags)
        val newPictures = List<Picture>(RAND.nextPositiveInt(maxListSize), { mock(Picture::class.java) }).toSet()
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(references = painting.references + newPictures)) }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.addReferences(painting = painting, references = newPictures)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun removeReferences() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags)
        val removePictures = mockRef.stream().filter { RAND.nextBoolean() }.collect(Collectors.toSet())
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(references = painting.references -
                removePictures)) }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.removeReferences(painting = painting, references = removePictures)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun addTags() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags)
        val newTags = List(RAND.nextPositiveInt(maxListSize), { RAND.nextString(maxListSize) }).toSet()
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(tags = painting.tags + newTags)) }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.addTags(painting = painting, tags = newTags)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    @Test
    fun removeTags() {
        //given
        val painting = Painting(id = ID, mainPicture = mockPicture, wip = mockWip, references = mockRef,
                finishingDate = mockDate, sellingInfo = mockSellInfo, tags = mockTags)
        val removeTags = mockTags.stream().filter { RAND.nextBoolean() }.collect(Collectors.toSet())
        val am = ArgumentMatcher<Painting> { equalPainting(it, painting.copy(tags = painting.tags -
                removeTags)) }
        given(databaseAdapter.updatePainting(argThatNonNull(am))).willReturn(mockPainting)
        //when
        val result = service.removeTags(painting = painting, tags = removeTags)
        //then
        then(databaseAdapter).should(only()).updatePainting(argThatNonNull(am))
        then(databaseAdapter).shouldHaveNoMoreInteractions()
        assertThat(result).isEqualTo(mockPainting)
    }

    private fun equalPainting(given: Painting?, expected: Painting): Boolean = given?.equals(expected) ?: false
}