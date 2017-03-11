package de.x4fyr.paiman.provider

import de.x4fyr.paiman.domain.dateTime.LocalDate


/**
 * @author x4fyr
 * Created on 3/4/17.
 */
interface DateTimeProvider {

    fun now(): LocalDate

    fun ofString(string: String): LocalDate
}