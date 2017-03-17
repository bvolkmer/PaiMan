package de.x4fyr.paiman.lib.services.conversions

import de.x4fyr.paiman.lib.domain.Painting
import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.Purchaser
import de.x4fyr.paiman.lib.domain.SellingInformation
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * @author de.x4fyr
 * Created on 3/10/17.
 */

fun Painting.toPropertiesMap(): Map<String, Any?> = mapOf<String, Any?>(
        "_id" to id?.toString(),
        "mainPicture" to mainPicture.id,
        "wip" to wip.map { it.id }.toList(),
        "references" to references.map { it.id }.toList(),
        "finishingDate" to finishingDate?.toISOString(),
        "sellingInfo" to sellingInfo?.toPropertiesMap(),
        "tags" to tags.toList(),
        "finished" to finished)


fun Painting(propertiesMap: Map<String, Any?>): Painting = Painting(
        id = propertiesMap["_id"]?.toString()?.toLong(),
        mainPicture = Picture(propertiesMap["mainPicture"].toString()),
        wip = (propertiesMap["wip"] as List<*>).filterIsInstance<String>().map(::Picture).toSet(),
        references = (propertiesMap["references"] as List<*>).filterIsInstance<String>().map(::Picture).toSet(),
        finishingDate = localDateOfIsoString(propertiesMap["finishingDate"].toString()),
        sellingInfo = if (propertiesMap["sellingInfo"] != null) SellingInformation((propertiesMap["sellingInfo"] as
                Map<*,*>)
                .asPropertiesMap()) else null,
        tags = (propertiesMap["tags"] as List<*>).filterIsInstance<String>().toSet(),
        finished = propertiesMap["finished"].toString().toBoolean()
)

fun SellingInformation.toPropertiesMap(): Map<String, Any?> = mapOf(
        "purchaser" to purchaser.toPropertiesMap(),
        "date" to date?.toISOString(),
        "price" to price)

fun SellingInformation(propertiesMap: Map<String, Any?>) = SellingInformation(
        purchaser = Purchaser((propertiesMap["purchaser"] as Map<*, *>).asPropertiesMap()),
        date = localDateOfIsoString(propertiesMap["date"].toString()),
        price = propertiesMap["price"].toString().toDouble())

fun Purchaser.toPropertiesMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "address" to address)

fun Purchaser(propertiesMap: Map<String, Any?>) = Purchaser(
        name = propertiesMap["name"].toString(),
        address = propertiesMap["address"].toString())

fun Map<*, *>.asPropertiesMap(): Map<String, Any?> {
    val result: MutableMap<String, Any?> = mutableMapOf()
    val keys = this.keys.filterIsInstance<String>()
    keys.forEach { if (this.contains(it)) result.put(it, this[it]) }
    return result
}

fun LocalDate.toISOString(): String = this.format(DateTimeFormatter.ISO_DATE)

fun localDateOfIsoString(isoString: String): LocalDate? {
    return if (isoString == "null" || isoString == "kotlin.Unit") {
        null
    } else {
        LocalDate.parse(isoString, DateTimeFormatter.ISO_DATE)
    }
}
