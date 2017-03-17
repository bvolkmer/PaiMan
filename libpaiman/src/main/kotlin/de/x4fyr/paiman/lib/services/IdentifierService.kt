package de.x4fyr.paiman.lib.services

/**
 * @author de.x4fyr
 * Created on 3/16/17.
 */
abstract class IdentifierService {
    abstract val PREFIX: String

    fun getPictureId(nativeId: String): String = "$PREFIX-$nativeId"

    fun hasRightPrefix(globalId: String): Boolean = globalId.startsWith(PREFIX)

    fun getNativeId(globalId: String): String = if (!hasRightPrefix(globalId)) {
        throw IllegalArgumentException("Wrong prefix for native id creation")
    } else globalId.drop(PREFIX.length)
}