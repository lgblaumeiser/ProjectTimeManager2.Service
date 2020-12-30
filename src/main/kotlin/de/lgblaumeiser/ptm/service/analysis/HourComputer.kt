// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

import de.lgblaumeiser.ptm.service.BookingService
import de.lgblaumeiser.ptm.service.model.Booking
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HourComputer(val bookingService: BookingService) : Analysis<HourAnalysisData> {
    override fun analyze(username: String, firstDay: String, firstDayAfter: String): List<HourAnalysisData> {
        val resultList = mutableListOf<HourAnalysisData>()
        var overtime = Duration.ZERO
        var totaltime = Duration.ZERO

        val bookingsMap = bookingService.getBookings(username, firstDay, firstDayAfter).groupBy { it.bookingday }
        var currentday = LocalDate.parse(firstDay)
        val endcondition = LocalDate.parse(firstDayAfter)
        while (currentday.isBefore(endcondition)) {
            val bookings = bookingsMap[currentday]
            if (bookings != null && !bookings.isEmpty()) {
                val resultEntry = createResultEntry(currentday, bookings, overtime, totaltime)
                resultList.add(resultEntry)
                resultEntry.overtime?.let { overtime = resultEntry.overtime }
                resultEntry.total?.let { totaltime = resultEntry.total }
            }
            currentday = currentday.plusDays(1L)
        }
        return resultList
    }

    private fun createResultEntry(
        currentDay: LocalDate,
        bookings: List<Booking>,
        overtime: Duration,
        totaltime: Duration
    ): HourAnalysisData {
        if (hasOpenBookings(bookings)) return HourAnalysisData(bookingday = currentDay, comment = INCOMPLETE_COMMENT)
        if (hasOverlaps(bookings)) return HourAnalysisData(bookingday = currentDay, comment = OVERLAPPING_COMMENT)
        var starttime = LocalTime.of(23, 59)
        var endtime = LocalTime.of(0, 0)
        var worktime: Duration = Duration.ZERO
        for (booking in bookings) {
            val currentworktime = calculateTimeframe(booking.starttime, booking.endtime!!)
            worktime = worktime.plusMinutes(currentworktime)
            if (booking.starttime.isBefore(starttime)) starttime = booking.starttime
            if (booking.endtime.isAfter(endtime)) endtime = booking.endtime
        }
        val presence = Duration.ofMinutes(calculateTimeframe(starttime, endtime))
        return HourAnalysisData(
            currentDay,
            starttime,
            endtime,
            presence,
            worktime,
            presence.minus(worktime),
            totaltime.plus(worktime),
            calculateOvertime(currentDay, overtime, worktime),
            calculateComment(presence, worktime)
        )
    }

    private fun calculateComment(presence: Duration, worktime: Duration): String {
        val worktimeMinutes = worktime.toMinutes()
        val breaktimeMinutes = presence.minus(worktime).toMinutes()
        if (worktimeMinutes > 600) {
            return WORKTIME_COMMENT
        }
        if (worktimeMinutes > 540 && breaktimeMinutes < 45) { // longer than 9 hours => 45 minutes break
            return BREAKTIME_COMMENT
        }
        if (worktimeMinutes > 360 && breaktimeMinutes < 30) { // longer than 6 hours => 30 minutes break
            return BREAKTIME_COMMENT
        }
        return ""
    }

    private fun calculateOvertime(day: LocalDate, overtime: Duration, worktime: Duration): Duration {
        var minutes = worktime
        if (isWeekDay(day)) minutes = minutes.minus(Duration.ofMinutes(480)) // Overtime is time after 8 hours
        return overtime.plus(minutes)
    }

    private fun isWeekDay(day: LocalDate) = !(day.dayOfWeek == DayOfWeek.SATURDAY || day.dayOfWeek == DayOfWeek.SUNDAY)

    private fun hasOverlaps(bookings: List<Booking>): Boolean {
        for (booking in bookings)
            for (toCheck in bookings)
                if (booking != toCheck && timeframeWithOverlap(
                        booking.starttime,
                        booking.endtime!!,
                        toCheck.starttime,
                        toCheck.endtime!!
                    )
                ) return true
        return false
    }

    private fun hasOpenBookings(bookings: List<Booking>) = bookings.filter { it.endtime == null }.isNotEmpty()
}

const val BREAKTIME_COMMENT = "Break too short!"
const val WORKTIME_COMMENT = "> 10 hours worktime!"
const val INCOMPLETE_COMMENT = "Day has unfinished bookings!"
const val OVERLAPPING_COMMENT = "Day has overlapping bookings!"

data class HourAnalysisData(
    val bookingday: LocalDate,
    val starttime: LocalTime? = null,
    val endtime: LocalTime? = null,
    val presence: Duration? = null,
    val worktime: Duration? = null,
    val breaktime: Duration? = null,
    val total: Duration? = null,
    val overtime: Duration? = null,
    val comment: String
) {
    fun bookingdayString() = dateToString(bookingday)
    fun starttimeString() = timeToString(starttime)
    fun endtimeString() = timeToString(endtime)
    fun presenceString() = durationToString(presence)
    fun worktimeString() = durationToString(worktime)
    fun breaktimeString() = durationToString(breaktime)
    fun totalString() = durationToString(total)
    fun overtimeString() = durationToString(overtime)
}

fun dateToString(date: LocalDate?) = date?.let { date.format(DateTimeFormatter.ISO_LOCAL_DATE) } ?: ""

fun timeToString(time: LocalTime?) = time?.let { time.format(DateTimeFormatter.ofPattern("HH:mm")) } ?: ""

fun durationToString(duration: Duration?): String {
    duration?.let {
        var minutes = duration.toMinutes()
        val pre = if (minutes < 0) '-' else ' '
        minutes = Math.abs(minutes)
        return String.format("%c%02d:%02d", pre, minutes / 60, minutes % 60);
    }
    return ""
}

fun timeframeWithOverlap(
    starttime1: LocalTime,
    endtime1: LocalTime,
    starttime2: LocalTime,
    endtime2: LocalTime
): Boolean {
    if (
        starttime1 == endtime2 ||
        starttime1.isAfter(endtime2) ||
        starttime2 == endtime1 ||
        starttime2.isAfter(endtime1)
    ) return false
    return true
}

fun calculateTimeframe(starttime: LocalTime, endtime: LocalTime): Long {
    return Duration.between(starttime, endtime).toMinutes()
}

