// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.store

import de.lgblaumeiser.ptm.service.model.Booking
import java.time.LocalDate

interface Store<T> {
    fun retrieveAll(user: String): List<T>

    // Throws IllegalStateException, if element is not in storage or not accesible for user
    fun retrieveById(user: String, id: Long): T

    fun create(data: T): T

    fun create(user: String, data: T): T

    fun update(user: String, data: T)

interface BookingStore: Store<Booking> {
    fun retrieveByBookingDays(user: String, days: List<LocalDate>): List<Booking>
}