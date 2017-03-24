package de.x4fyr.paiman.lib.adapter.database

/**
 * Created by x4fyr on 3/22/17.
 */

internal const val DB_NAME = "paintings"
internal const val TYPE = "type"
internal const val ID = "_id"
internal object TYPES {
    const val PAINTING = "painting"
}
internal object PAINTING {
    const val TITLE = "title"
    const val MAIN_PICTURE = "mainPicture"
    const val WIP = "wip"
    const val REFERENCES = "references"
    const val FINISHING_DATE = "finishingDate"
    const val SELLING_INFO = "sellingInfo"
    const val TAGS = "tags"
    const val FINISHED = "finished"
}
internal object SELLING_INFO {
    const val PURCHASER = "purchaser"
    const val DATE = "date"
    const val PRICE = "price"
}
internal object PURCHASER {
    const val NAME = "name"
    const val ADDRESS = "address"
}
