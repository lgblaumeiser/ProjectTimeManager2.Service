// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.store

import de.lgblaumeiser.ptm.service.model.Booking
import java.time.LocalDate

interface Store<T> {
    fun retrieveAll(user: String): List<T>

    // Throws IllegalArgumentException, if element is not in storage or not accessible for user
    fun retrieveById(user: String, id: Long): T

    fun create(data: T): T

    fun update(data: T)

    fun delete(id: Long)
}

interface BookingStore: Store<Booking> {
    fun retrieveByBookingDays(user: String, days: List<LocalDate>): List<Booking>
}