// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Booking
import java.time.LocalDate
import java.time.LocalTime

const val testBookingUser1 = "userid1"
const val testBookingUser2 = "userid2"

const val testDate1 = "2020-12-01"
const val testDate2 = "2020-12-03"

const val testTime1 = "08:00"
const val testTime2 = "09:30"
const val testTime3 = "10:00"
const val testTime4 = "12:00"

const val testActivityId1 = 1L
const val testActivityId2 = 2L

const val testComment = "Cool comment for booking"

const val testDuration = 45L

val bookingStore = BookingTestStore()
val bookingService = BookingService(bookingStore)

fun initializeBookingService() {
    bookingStore.clear()
}

val testBooking1 =
    Booking(-1L, LocalDate.parse(testDate1), LocalTime.parse(testTime1), null, testActivityId1, testBookingUser1, "")
val testBooking2 = Booking(
    -1L,
    LocalDate.parse(testDate1),
    LocalTime.parse(testTime2),
    LocalTime.parse(testTime3),
    testActivityId2,
    testBookingUser1,
    testComment
)

fun addMinimalBooking() = bookingService.addBooking(
    user = testBookingUser1,
    bookingday = testDate1,
    starttime = testTime1,
    activity = testActivityId1
)

fun addCompleteBooking() =
    bookingService.addBooking(testBookingUser1, testDate1, testTime2, testTime3, testActivityId2, testComment)

fun addLongBooking() =
    bookingService.addBooking(testBookingUser1, testDate1, testTime1, testTime4, testActivityId1, testComment)
