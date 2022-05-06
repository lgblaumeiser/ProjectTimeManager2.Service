// SPDX-FileCopyrightText: 2021, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.analysis.ActivityComputer
import de.lgblaumeiser.ptm.service.analysis.HourComputer
import de.lgblaumeiser.ptm.service.analysis.ProjectComputer

class AnalysisService(activityService: ActivityService, bookingService: BookingService) {
    private val projectAnalysis = ProjectComputer(activityService, bookingService)
    private val activityAnalysis = ActivityComputer(activityService, bookingService)
    private val hourAnalysis = HourComputer(bookingService)

    fun runProjectAnalysis(username: String, firstDay: String, firstDayAfter: String) =
        projectAnalysis.analyze(username, firstDay, firstDayAfter).map {
            ProjectAnalyisResultElement(
                projectId = it.projectId,
                projectName = it.projectName,
                minutes = it.minutesString(),
                percentage = it.percentageString(),
                comment = it.comment
            )
        }.toList()

    fun runActivityAnalysis(username: String, firstDay: String, firstDayAfter: String) =
        activityAnalysis.analyze(username, firstDay, firstDayAfter).map {
            ActivityAnalysisResultElement(
                projectId = it.projectId,
                projectName = it.projectName,
                activityId = it.activityId!!,
                activityName = it.activityName!!,
                minutes = it.minutesString(),
                percentage = it.percentageString(),
                comment = it.comment
            )
        }.toList()

    fun runHourAnalysis(username: String, firstDay: String, firstDayAfter: String) =
        hourAnalysis.analyze(username, firstDay, firstDayAfter).map{
            HourAnalysisResultElement(
                bookingday = it.bookingdayString(),
                starttime = it.starttimeString(),
                endtime = it.endtimeString(),
                presence = it.presenceString(),
                worktime = it.worktimeString(),
                breaktime = it.breaktimeString(),
                total = it.totalString(),
                overtime = it.overtimeString(),
                comment = it.comment
            )
        }.toList()
}

data class ProjectAnalyisResultElement(
    val projectId: String,
    val projectName: String,
    val minutes: String,
    val percentage: String,
    val comment: String
)

data class ActivityAnalysisResultElement(
    val projectId: String,
    val projectName: String,
    val activityId: String,
    val activityName: String,
    val minutes: String,
    val percentage: String,
    val comment: String
)

data class HourAnalysisResultElement(
    val bookingday: String,
    val starttime: String,
    val endtime: String,
    val presence: String,
    val worktime: String,
    val breaktime: String,
    val total: String,
    val overtime: String,
    val comment: String
)