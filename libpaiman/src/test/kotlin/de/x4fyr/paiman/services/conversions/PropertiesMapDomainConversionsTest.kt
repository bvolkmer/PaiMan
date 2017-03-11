package de.x4fyr.paiman.services.conversions

import de.x4fyr.paiman.domain.Picture
import de.x4fyr.paiman.domain.dateTime.LocalDate
import de.x4fyr.paiman.provider.DateTimeProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import util.eqNonNull
import util.nextPositiveInt
import util.nextPositiveLong
import util.nextString
import java.util.Random

/**
 * @author x4fyr
 * Created on 3/11/17.
 */
class PropertiesMapDomainConversionsTest {

    companion object {
        val RAND = Random()
        val MAX_INT = 1000
        val ID = RAND.nextPositiveLong()
        val MAIN_PICTURE = Picture(RAND.nextPositiveLong())
        val WIP = List(RAND.nextPositiveInt(MAX_INT), { Picture(RAND.nextPositiveLong()) }).toSet()
        val REF = List(RAND.nextPositiveInt(MAX_INT), { Picture(RAND.nextPositiveLong()) }).toSet()
        val FINISHING_DATE_STRING = RAND.nextString()
        val PU_NAME = RAND.nextString()
        val PU_ADDRESS = RAND.nextString()
        val SELL_DATE_STRING = RAND.nextString()
        val SELL_PRICE = RAND.nextDouble()
        val TAGS = List(RAND.nextPositiveInt(MAX_INT), { RAND.nextString() }).toSet()
        val FINISHED = RAND.nextBoolean()
    }

    @Test
    fun PaintingConversionNonNullTest() {
        //given
        val dateTimeProvider = mock(DateTimeProvider::class.java)
        val finishingDateMock = mock(LocalDate::class.java)
        given(finishingDateMock.toString()).willReturn(FINISHING_DATE_STRING)
        given(dateTimeProvider.ofString(eqNonNull(FINISHING_DATE_STRING))).willReturn(finishingDateMock)
        val sellDateMock = mock(LocalDate::class.java)
        given(sellDateMock.toString()).willReturn(SELL_DATE_STRING)
        given(dateTimeProvider.ofString(eqNonNull(FINISHING_DATE_STRING))).willReturn(finishingDateMock)
        val painting = de.x4fyr.paiman.domain.Painting(
                id = ID,
                mainPicture = MAIN_PICTURE,
                wip = WIP,
                references = REF,
                finishingDate = finishingDateMock,
                sellingInfo = de.x4fyr.paiman.domain.SellingInformation(
                        purchaser = de.x4fyr.paiman.domain.Purchaser(
                                name = PU_NAME,
                                address = PU_ADDRESS
                        ),
                        date = finishingDateMock,
                        price = SELL_PRICE
                ),
                tags = TAGS,
                finished = FINISHED
        )
        //when
        val result = Painting(painting.toPropertiesMap(), dateTimeProvider)
        //then
        assertThat(result).isEqualTo(painting)
    }

    @Test
    fun PaintingConversionSellDateNullTest() {
        //given
        val dateTimeProvider = mock(DateTimeProvider::class.java)
        val finishingDateMock = mock(LocalDate::class.java)
        given(finishingDateMock.toString()).willReturn(FINISHING_DATE_STRING)
        given(dateTimeProvider.ofString(eqNonNull(FINISHING_DATE_STRING))).willReturn(finishingDateMock)
        val sellDateMock = mock(LocalDate::class.java)
        given(sellDateMock.toString()).willReturn(SELL_DATE_STRING)
        given(dateTimeProvider.ofString(eqNonNull(FINISHING_DATE_STRING))).willReturn(finishingDateMock)
        val painting = de.x4fyr.paiman.domain.Painting(
                id = ID,
                mainPicture = MAIN_PICTURE,
                wip = WIP,
                references = REF,
                finishingDate = finishingDateMock,
                sellingInfo = de.x4fyr.paiman.domain.SellingInformation(
                        purchaser = de.x4fyr.paiman.domain.Purchaser(
                                name = PU_NAME,
                                address = PU_ADDRESS
                        ),
                        date = null,
                        price = SELL_PRICE
                ),
                tags = TAGS,
                finished = FINISHED
        )
        //when
        val result = Painting(painting.toPropertiesMap(), dateTimeProvider)
        //then
        assertThat(result).isEqualTo(painting)
    }

    @Test
    fun PaintingConversionAllNullTest() {
        //given
        val dateTimeProvider = mock(DateTimeProvider::class.java)
        val finishingDateMock = mock(LocalDate::class.java)
        given(finishingDateMock.toString()).willReturn(FINISHING_DATE_STRING)
        given(dateTimeProvider.ofString(eqNonNull(FINISHING_DATE_STRING))).willReturn(finishingDateMock)
        val sellDateMock = mock(LocalDate::class.java)
        given(sellDateMock.toString()).willReturn(SELL_DATE_STRING)
        given(dateTimeProvider.ofString(eqNonNull(FINISHING_DATE_STRING))).willReturn(finishingDateMock)
        val painting = de.x4fyr.paiman.domain.Painting(
                id = null,
                mainPicture = MAIN_PICTURE,
                wip = WIP,
                references = REF,
                finishingDate = null,
                sellingInfo = null,
                tags = TAGS,
                finished = FINISHED
        )
        //when
        val result = Painting(painting.toPropertiesMap(), dateTimeProvider)
        //then
        assertThat(result).isEqualTo(painting)
    }
}