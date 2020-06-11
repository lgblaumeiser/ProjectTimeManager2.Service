// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

import java.time.LocalDate
import java.time.LocalTime

data class Booking (
    val id: Long = -1,
    val bookingday: LocalDate,
    val starttime: LocalTime,
    val endtime: LocalTime? = null,
    val activity: Long,
    val user: String,
    val comment: String = ""
)
