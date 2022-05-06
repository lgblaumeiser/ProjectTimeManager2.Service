// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

import java.time.LocalDate
import java.time.LocalTime

data class Booking(
    val id: Long = -1,
    val bookingday: LocalDate,
    val starttime: LocalTime,
    val endtime: LocalTime? = null,
    val activity: Long,
    val user: String,
    val comment: String = ""
) {
    init {
        require(user.isNotBlank()) { "user field must not be empty for booking" }
        require(endtime?.isAfter(starttime) ?: true) { "starttime of a booking must be prior to endtime" }
        require(activity > 0L)
    }
}
