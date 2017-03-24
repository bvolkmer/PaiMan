package de.x4fyr.paiman.app.ui.detail

import de.x4fyr.paiman.lib.domain.Picture
import de.x4fyr.paiman.lib.domain.SellingInformation
import java.io.InputStream
import java.time.LocalDate

/**
 * Created by x4fyr on 3/23/17.
 */
data class Model(val id: String,
                 val title: String,
                 val mainPicture: InputStream,
                 val wip: List<InputStream>,
                 val references: List<InputStream>,
                 val finishingDate: LocalDate?,
                 val sellingInformation: SellingInformation?,
                 val tags: Set<String>, val finished: Boolean)
