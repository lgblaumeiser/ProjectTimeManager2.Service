// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Booking
import de.lgblaumeiser.ptm.service.store.Store
import java.time.LocalDate
import java.time.LocalTime

class BookingService(val store: Store<Booking>) {
    fun getBookings(user: String) = store
        .retrieveAll(user)
        .sortedWith(compareBy<Booking> { it.bookingday }.thenBy { it.starttime })

    fun getBookings(user: String, startday: String, endday: String? = null): List<Booking> = store
        .retrieveByProperty(user, "bookingday", computeDays(startday, endday))
        .sortedWith(compareBy<Booking> { it.bookingday }.thenBy { it.starttime })

    private fun computeDays(startday: String, endday: String?): List<LocalDate> {
        val parsedstartday = LocalDate.parse(startday)
        val parsedendday = LocalDate.parse(endday) ?: parsedstartday.plusDays(1L)
        val listofdays = mutableListOf<LocalDate>()
        var currentday = parsedstartday
        do {
            listofdays.add(currentday)
            currentday = currentday.plusDays(1L)
        } while (currentday.isBefore(parsedendday))
        return listofdays
    }

    fun getBookingById(user: String, id: Long) = store
        .retrieveById(user, id)

    fun addBooking(
        user: String,
        bookingday: String,
        starttime: String,
        endtime: String? = null,
        activity: Long,
        comment: String = ""
    ): Booking {
        retrieveOpenBooking(user, bookingday)?.let { changeBooking(id = it.id, user = it.user, endtime = starttime) }
        return store.create(
            Booking(
                user = user,
                bookingday = LocalDate.parse(bookingday),
                starttime = LocalTime.parse(starttime),
                endtime = endtime?.let { LocalTime.parse(endtime) },
                activity = activity,
                comment = comment
            )
        )
    }

    private fun retrieveOpenBooking(user: String, bookingday: String) =
        getBookings(user = user, startday = bookingday).find { it.endtime == null }


    fun changeBooking(
        user: String,
        bookingday: String? = null,
        starttime: String? = null,
        endtime: String? = null,
        activity: Long? = null,
        comment: String? = null,
        id: Long
    ) = getBookingById(user, id).let {
        store.update(
            Booking(
                id = it.id,
                user = it.user,
                bookingday = LocalDate.parse(bookingday) ?: it.bookingday,
                starttime = LocalTime.parse(starttime) ?: it.starttime,
                endtime = LocalTime.parse(endtime) ?: it.endtime,
                activity = activity ?: it.activity,
                comment = comment ?: it.comment
            )
        )
    }

    fun splitBooking(
        user: String,
        starttime: String,
        duration: Long = 30L,
        id: Long
    ): Booking {
        val booking = getBookingById(user, id)
        val parsedstarttime = LocalTime.parse(starttime)
        val firstBooking = booking.copy(endtime = parsedstarttime)
        val secondBooking = booking.copy(starttime = parsedstarttime.plusMinutes(duration))
        store.update(firstBooking)
        return store.create(secondBooking)
    }

    fun deleteBooking(user: String, id: Long) =
        getBookingById(user, id).let { store.delete(user, id) }
}
