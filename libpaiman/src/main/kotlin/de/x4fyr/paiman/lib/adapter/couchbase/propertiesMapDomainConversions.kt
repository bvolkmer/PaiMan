package de.x4fyr.paiman.lib.adapter.couchbase

import de.x4fyr.paiman.lib.domain.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * @author de.x4fyr
 * Created on 3/10/17.
 */


internal fun Painting.toPropertiesMap(): Map<String, Any?> = mapOf<String, Any?>(
        TYPE to TYPES.PAINTING,
        PAINTING.TITLE to title,
        PAINTING.MAIN_PICTURE to mainPicture.id,
        PAINTING.WIP to wip.map { it.id }.toList(),
        PAINTING.REFERENCES to references.map { it.id }.toList(),
        PAINTING.FINISHING_DATE to finishingDate?.toISOString(),
        PAINTING.SELLING_INFO to sellingInfo?.toPropertiesMap(),
        PAINTING.TAGS to tags.toList(),
        PAINTING.FINISHED to finished)

internal fun SavedPainting.toPropertiesMap(): Map<String, Any?> = mapOf<String, Any?>(
        TYPE to TYPES.PAINTING,
        ID to id,
        PAINTING.TITLE to title,
        PAINTING.MAIN_PICTURE to mainPicture.id,
        PAINTING.WIP to wip.map { it.id }.toList(),
        PAINTING.REFERENCES to references.map { it.id }.toList(),
        PAINTING.FINISHING_DATE to finishingDate?.toISOString(),
        PAINTING.SELLING_INFO to sellingInfo?.toPropertiesMap(),
        PAINTING.TAGS to tags.toList(),
        PAINTING.FINISHED to finished)


internal fun SavedPainting(propertiesMap: Map<String, Any?>?): SavedPainting? = if (propertiesMap == null) null
else if (propertiesMap[TYPE] == TYPES.PAINTING)
    SavedPainting(
            id = propertiesMap[ID].toString(),
            title = propertiesMap[PAINTING.TITLE].toString(),
            wip = (propertiesMap[PAINTING.WIP] as List<*>).filterIsInstance<String>().map(::Picture).toSet(),
            references = (propertiesMap[PAINTING.REFERENCES] as List<*>).filterIsInstance<String>()
                    .map(::Picture).toSet(),
            finishingDate = localDateOfIsoString(propertiesMap[PAINTING.FINISHING_DATE].toString()),
            sellingInfo = if (propertiesMap[PAINTING.SELLING_INFO] != null) SellingInformation(
                    (propertiesMap[PAINTING.SELLING_INFO] as Map<*, *>).asPropertiesMap()) else null,
            tags = (propertiesMap[PAINTING.TAGS] as List<*>).filterIsInstance<String>().toSet(),
            mainPicture = Picture(propertiesMap[PAINTING.MAIN_PICTURE].toString()),
            finished = propertiesMap[PAINTING.FINISHED].toString().toBoolean())
else
    throw IllegalArgumentException("Expected map of properties with type == \"${TYPES.PAINTING}\". Got " +
            "${propertiesMap[TYPE]}")

private fun SellingInformation.toPropertiesMap(): Map<String, Any?> = mapOf(
        SELLING_INFO.PURCHASER to purchaser.toPropertiesMap(),
        SELLING_INFO.DATE to date.toISOString(),
        SELLING_INFO.PRICE to price)

private fun SellingInformation(propertiesMap: Map<String, Any?>) = de.x4fyr.paiman.lib.domain.SellingInformation(
        purchaser = Purchaser((propertiesMap[SELLING_INFO.PURCHASER] as Map<*, *>).asPropertiesMap()),
        date = localDateOfIsoString(propertiesMap[SELLING_INFO.DATE].toString())!!,
        price = propertiesMap[SELLING_INFO.PRICE].toString().toDouble())

private fun Purchaser.toPropertiesMap(): Map<String, Any?> = mapOf(
        PURCHASER.NAME to name,
        PURCHASER.ADDRESS to address)

private fun Purchaser(propertiesMap: Map<String, Any?>) = Purchaser(
        name = propertiesMap[PURCHASER.NAME].toString(),
        address = propertiesMap[PURCHASER.ADDRESS].toString())

private fun Map<*, *>.asPropertiesMap(): Map<String, Any?> {
    val result: MutableMap<String, Any?> = mutableMapOf()
    val keys = this.keys.filterIsInstance<String>()
    keys.forEach { if (this.contains(it)) result.put(it, this[it]) }
    return result
}

private fun LocalDate.toISOString(): String = this.format(org.threeten.bp.format.DateTimeFormatter.ISO_DATE)

private fun localDateOfIsoString(isoString: String): LocalDate? {
    return if (isoString == "null" || isoString == "kotlin.Unit") {
        null
    } else {
        LocalDate.parse(isoString, DateTimeFormatter.ISO_DATE)
    }
}
