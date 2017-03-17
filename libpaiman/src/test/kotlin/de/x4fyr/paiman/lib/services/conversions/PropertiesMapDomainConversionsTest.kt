package de.x4fyr.paiman.lib.services.conversions

import de.x4fyr.paiman.lib.domain.Picture
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import util.nextPositiveInt
import util.nextPositiveLong
import util.nextString
import java.util.Random
import java.time.LocalDate

/**
 * @author de.x4fyr
 * Created on 3/11/17.
 */
class PropertiesMapDomainConversionsTest {

    companion object {
        val RAND = Random()
        val MAX_INT = 1000
        val ID = RAND.nextPositiveLong()
        val MAIN_PICTURE = Picture(RAND.nextString())
        val WIP = List(RAND.nextPositiveInt(MAX_INT), { Picture(RAND.nextString()) }).toSet()
        val REF = List(RAND.nextPositiveInt(MAX_INT), { Picture(RAND.nextString()) }).toSet()
        var FINISHING_DATE = LocalDate.now()!!
        val PU_NAME = RAND.nextString()
        val PU_ADDRESS = RAND.nextString()
        var SELL_DATE = LocalDate.now()!!.plusDays(1)!!
        val SELL_PRICE = RAND.nextDouble()
        val TAGS = List(RAND.nextPositiveInt(MAX_INT), { RAND.nextString() }).toSet()
        val FINISHED = RAND.nextBoolean()
    }

    @Test
    fun PaintingConversionNonNullTest() {
        //given
        val painting = de.x4fyr.paiman.lib.domain.Painting(
                id = ID,
                mainPicture = MAIN_PICTURE,
                wip = WIP,
                references = REF,
                finishingDate = FINISHING_DATE,
                sellingInfo = de.x4fyr.paiman.lib.domain.SellingInformation(
                        purchaser = de.x4fyr.paiman.lib.domain.Purchaser(
                                name = PU_NAME,
                                address = PU_ADDRESS
                        ),
                        date = SELL_DATE,
                        price = SELL_PRICE
                ),
                tags = TAGS,
                finished = FINISHED
        )
        //when
        val result = Painting(painting.toPropertiesMap())
        //then
        assertThat(result).isEqualTo(painting)
    }

    @Test
    fun PaintingConversionSellDateNullTest() {
        //given
        val painting = de.x4fyr.paiman.lib.domain.Painting(
                id = ID,
                mainPicture = MAIN_PICTURE,
                wip = WIP,
                references = REF,
                finishingDate = FINISHING_DATE,
                sellingInfo = de.x4fyr.paiman.lib.domain.SellingInformation(
                        purchaser = de.x4fyr.paiman.lib.domain.Purchaser(
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
        val result = Painting(painting.toPropertiesMap())
        //then
        assertThat(result).isEqualTo(painting)
    }

    @Test
    fun PaintingConversionAllNullTest() {
        //given
        val painting = de.x4fyr.paiman.lib.domain.Painting(
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
        val result = Painting(painting.toPropertiesMap())
        //then
        assertThat(result).isEqualTo(painting)
    }
}