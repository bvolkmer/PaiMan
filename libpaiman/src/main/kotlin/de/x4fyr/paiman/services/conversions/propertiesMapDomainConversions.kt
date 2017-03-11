package de.x4fyr.paiman.services.conversions

import de.x4fyr.paiman.domain.Painting
import de.x4fyr.paiman.domain.Picture
import de.x4fyr.paiman.domain.Purchaser
import de.x4fyr.paiman.domain.SellingInformation
import de.x4fyr.paiman.provider.DateTimeProvider

/**
 * @author x4fyr
 * Created on 3/10/17.
 */

fun Painting.toPropertiesMap(): Map<String, Any?> = mapOf<String, Any?>(
        "_id" to id?.toString(),
        "mainPicture" to mainPicture.id.toString(),
        "wip" to wip.map { it.id.toString() }.toList(),
        "references" to references.map { it.id.toString() }.toList(),
        "finishingDate" to finishingDate?.toString(),
        "sellingInfo" to sellingInfo?.toPropertiesMap(),
        "tags" to tags.toList(),
        "finished" to finished)


fun Painting(propertiesMap: Map<String, Any?>, dateTimeProvider: DateTimeProvider): Painting = Painting(
        id = propertiesMap["_id"]?.toString()?.toLong(),
        mainPicture = Picture(propertiesMap["mainPicture"].toString().toLong()),
        wip = (propertiesMap["wip"] as List<*>).filterIsInstance<String>().map { Picture(it.toLong()) }.toSet(),
        references = (propertiesMap["references"] as List<*>).filterIsInstance<String>().map { Picture(it.toLong()) }.toSet(),
        finishingDate = dateTimeProvider.ofString(propertiesMap["finishingDate"].toString()),
        sellingInfo = if (propertiesMap["sellingInfo"] != null) SellingInformation((propertiesMap["sellingInfo"] as
                Map<*,*>)
                .asPropertiesMap(),
                dateTimeProvider) else null,
        tags = (propertiesMap["tags"] as List<*>).filterIsInstance<String>().toSet(),
        finished = propertiesMap["finished"].toString().toBoolean()
)

fun SellingInformation.toPropertiesMap(): Map<String, Any?> = mapOf(
        "purchaser" to purchaser.toPropertiesMap(),
        "date" to date.toString(),
        "price" to price)

fun SellingInformation(propertiesMap: Map<String, Any?>, dateTimeProvider: DateTimeProvider)= SellingInformation(
        purchaser = Purchaser((propertiesMap["purchaser"] as Map<*, *>).asPropertiesMap()),
        date = dateTimeProvider.ofString(propertiesMap["date"].toString()),
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
