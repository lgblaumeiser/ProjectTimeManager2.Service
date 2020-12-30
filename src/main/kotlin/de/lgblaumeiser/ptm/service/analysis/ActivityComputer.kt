// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

import de.lgblaumeiser.ptm.service.ActivityService
import de.lgblaumeiser.ptm.service.BookingService
import de.lgblaumeiser.ptm.service.model.Booking
import java.time.Duration
import java.time.temporal.ChronoUnit

class ActivityComputer(
    activityService: ActivityService,
    bookingService: BookingService
) : AbstractComputer<ActivityAnalysisData>(activityService, bookingService) {
    override fun analyze(username: String, firstDay: String, firstDayAfter: String): List<ActivityAnalysisData> {
        return emptyList()
    }
//    fun analyze(username: String, firstDay: String, firstDayAfter: String) =
//        calculateTimeMapping(
//            bookingService.getBookings(username, firstDay, firstDayAfter),
//            oneDayOnly(firstDay, firstDayAfter)
//        )
//
//    private fun calculateTimeMapping(bookings: List<Booking>, withComments: Boolean): Any {
//        val analysisData = mutableMapOf<String, ActivityAnalysisData>()
//        var totalLength = Duration.ZERO
//        bookings.forEach {
//            val length = computeLength(it)
//            totalLength = totalLength.plus(length)
//            val newData = addToDataSet()
//
//
//        }
//
//    }
//
//    private fun computeLength(booking: Booking) = booking.endtime?.let {
//        Duration.ofMinutes(ChronoUnit.MINUTES.between(booking.starttime, booking.endtime))
//    } ?: Duration.ZERO

}

data class ActivityAnalysisData(
    val projectName: String,
    val projectId: String,
    val activityName: String,
    val activityId: String,
    val minutes: Int,
    val percentage: Int,
    val comment: String
)

