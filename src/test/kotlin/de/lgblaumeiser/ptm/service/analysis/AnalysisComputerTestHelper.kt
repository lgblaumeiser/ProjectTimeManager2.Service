// SPDX-FileCopyrightText: 2020, 2021 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

import de.lgblaumeiser.ptm.service.activityService
import de.lgblaumeiser.ptm.service.bookingService
import de.lgblaumeiser.ptm.service.initializeActivityService
import de.lgblaumeiser.ptm.service.initializeBookingService
import java.time.LocalDate
import java.time.LocalTime

const val testAnalysisUsername = "CoolUser"

const val testAnalysisProjectname1 = "a"
const val testAnalysisProjectname2 = "b"
const val testAnalysisActivityname1 = "c"
const val testAnalysisActivityname2 = "d"
const val testAnalysisActivityname3 = "e"
const val testAnalysisProjectid1 = "f"
const val testAnalysisProjectid2 = "g"
const val testAnalysisActivityid1 = "h"
const val testAnalysisActivityid2 = "i"
const val testAnalysisActivityid3 = "j"

const val testAnalysisActivityObjectId1 = 1L
const val testAnalysisActivityObjectId2 = 2L
const val testAnalysisActivityObjectId3 = 3L

val testAnalysisDate1 = LocalDate.of(2017, 3, 1)
val testAnalysisDate2 = LocalDate.of(2017, 3, 6)
val testAnalysisDate3 = LocalDate.of(2017, 3, 9)
val testAnalysisDate4 = LocalDate.of(2017, 3, 15)
val testAnalysisDate5 = LocalDate.of(2017, 3, 24)
val testAnalysisDate6 = LocalDate.of(2017, 3, 28)

val testAnalysisTime1 = LocalTime.of(12, 34)
val testAnalysisTime2 = LocalTime.of(13, 57)
val testAnalysisTime3 = LocalTime.of(14, 35)
val testAnalysisTime4 = LocalTime.of(8, 15)
val testAnalysisTime5 = LocalTime.of(17, 25)
val testAnalysisTime6 = LocalTime.of(9, 42)
val testAnalysisTime7 = LocalTime.of(15, 39)
val testAnalysisTime8 = LocalTime.of(18, 45)
val testAnalysisTime9 = LocalTime.of(21, 45)

val testAnalysisComment1 = "Comment 1"
val testAnalysisComment2 = "Comment 2"
val testAnalysisComment3 = "Comment 3"

fun createTestdatabase(): Unit {
    initializeActivityService()
    activityService.addActivity(
        testAnalysisUsername,
        testAnalysisProjectname1,
        testAnalysisProjectid1,
        testAnalysisActivityname1,
        testAnalysisActivityid1
    )
    activityService.addActivity(
        testAnalysisUsername,
        testAnalysisProjectname2,
        testAnalysisProjectid2,
        testAnalysisActivityname2,
        testAnalysisActivityid2
    )
    activityService.addActivity(
        testAnalysisUsername,
        testAnalysisProjectname1,
        testAnalysisProjectid1,
        testAnalysisActivityname3,
        testAnalysisActivityid3
    )

    initializeBookingService()
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate1),
        starttime = timeToString(testAnalysisTime1),
        endtime = timeToString(testAnalysisTime2),
        activity = testAnalysisActivityObjectId1,
        comment = testAnalysisComment1
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate1),
        starttime = timeToString(testAnalysisTime2),
        endtime = timeToString(testAnalysisTime3),
        activity = testAnalysisActivityObjectId2
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate2),
        starttime = timeToString(testAnalysisTime4),
        endtime = timeToString(testAnalysisTime6),
        activity = testAnalysisActivityObjectId3,
        comment = testAnalysisComment2
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate2),
        starttime = timeToString(testAnalysisTime7),
        endtime = timeToString(testAnalysisTime8),
        activity = testAnalysisActivityObjectId1,
        comment = testAnalysisComment3
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate3),
        starttime = timeToString(testAnalysisTime6),
        endtime = timeToString(testAnalysisTime3),
        activity = testAnalysisActivityObjectId2
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate3),
        starttime = timeToString(testAnalysisTime3),
        endtime = timeToString(testAnalysisTime5),
        activity = testAnalysisActivityObjectId3
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate4),
        starttime = timeToString(testAnalysisTime4),
        endtime = timeToString(testAnalysisTime7),
        activity = testAnalysisActivityObjectId1,
        comment = testAnalysisComment2
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate4),
        starttime = timeToString(testAnalysisTime7),
        endtime = timeToString(testAnalysisTime8),
        activity = testAnalysisActivityObjectId2,
        comment = testAnalysisComment3
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate5),
        starttime = timeToString(testAnalysisTime4),
        activity = testAnalysisActivityObjectId3
    )
    bookingService.addBooking(
        user = testAnalysisUsername,
        bookingday = dateToString(testAnalysisDate6),
        starttime = timeToString(testAnalysisTime6),
        endtime = timeToString(testAnalysisTime8),
        activity = testAnalysisActivityObjectId1
    )
}
