// SPDX-FileCopyrightText: 2020, 2021, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

import de.lgblaumeiser.ptm.service.ActivityService
import de.lgblaumeiser.ptm.service.BookingService
import de.lgblaumeiser.ptm.service.model.Booking
import java.time.Duration
import java.time.LocalDate

abstract class BaseActivityComputer(
    private val activityService: ActivityService,
    private val bookingService: BookingService
) : Analysis<ActivityAnalysisData> {
    override fun analyze(username: String, firstDay: String, firstDayAfter: String): List<ActivityAnalysisData> {
        var resultList =
            bookingService.getBookings(username, firstDay, firstDayAfter).groupBy { it.activity }.values.mapNotNull {
                calculateResultDataForActivity(username, it, withComments(firstDay, firstDayAfter))
            }.toList()

        resultList = aggregateForTarget(resultList)

        val totalMinutes = resultList.map { it.minutes }.reduce(Duration::plus)
        val totalMinutesValue = totalMinutes.toMinutes().toDouble()
        resultList = resultList.map { calculatePercentage(it, totalMinutesValue) }.toMutableList()

        resultList.add(
            ActivityAnalysisData(
                "Total",
                "",
                "",
                "",
                totalMinutes,
                100.0,
                ""
            )
        )
        return resultList
    }

    private fun calculatePercentage(data: ActivityAnalysisData, totalMinutes: Double) =
        data.copy(percentage = data.minutes.toMinutes().toDouble() * 100.0 / totalMinutes)

    protected abstract fun aggregateForTarget(resultList: List<ActivityAnalysisData>): List<ActivityAnalysisData>

    private fun withComments(firstDay: String, secondDay: String) =
        LocalDate.parse(firstDay).plusDays(1L) == LocalDate.parse(secondDay)

    private fun calculateResultDataForActivity(
        username: String,
        bookings: List<Booking>,
        withComments: Boolean
    ): ActivityAnalysisData? {
        val activity = activityService.getActivityById(username, bookings.first().activity)
        val relevant = bookings.filter { it.endtime != null }
        if (relevant.isEmpty()) return null
        val minutes = relevant.map { Duration.between(it.starttime, it.endtime) }.reduce(Duration::plus)
        val comment = if (withComments) relevant
            .map { it.comment }
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(separator = ", ") else ""

        return ActivityAnalysisData(
            activity.projectname,
            activity.projectid,
            activity.activityname,
            activity.activityid,
            minutes,
            0.0,
            comment
        )
    }
}

class ActivityComputer(
    activityService: ActivityService,
    bookingService: BookingService
) : BaseActivityComputer(activityService, bookingService) {
    override fun aggregateForTarget(resultList: List<ActivityAnalysisData>) =
        resultList.sortedWith(compareBy<ActivityAnalysisData> { it.projectId }.thenBy { it.activityId }).toList()
}

class ProjectComputer(
    activityService: ActivityService,
    bookingService: BookingService
) : BaseActivityComputer(activityService, bookingService) {
    override fun aggregateForTarget(resultList: List<ActivityAnalysisData>) =
        resultList.groupingBy { it.projectId }.reduce(this::sumData).toMap().values
            .sortedWith(compareBy { it.projectId }).toList()

    private fun sumData(key: String, data1: ActivityAnalysisData, data2: ActivityAnalysisData): ActivityAnalysisData {
        val minutes = data1.minutes.plus(data2.minutes)
        val comment = listOf(data1.comment, data2.comment).distinct().joinToString(separator = ", ")

        return ActivityAnalysisData(
            projectId = key,
            projectName = data1.projectName,
            minutes = minutes,
            comment = comment
        )
    }
}

data class ActivityAnalysisData(
    val projectName: String,
    val projectId: String,
    val activityName: String? = null,
    val activityId: String? = null,
    val minutes: Duration,
    val percentage: Double = 0.0,
    val comment: String
) {
    fun minutesString() = durationToString(minutes)
    fun percentageString() = "$percentage%"
}

