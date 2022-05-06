// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

interface Analysis<T> {
    fun analyze(username: String, firstDay: String, firstDayAfter: String) : List<T>
}

fun dateToString(date: LocalDate?) = date?.let { date.format(DateTimeFormatter.ISO_LOCAL_DATE) } ?: ""

fun timeToString(time: LocalTime?) = time?.let { time.format(DateTimeFormatter.ofPattern("HH:mm")) } ?: ""

fun durationToString(duration: Duration?): String {
    duration?.let {
        var minutes = duration.toMinutes()
        val pre = if (minutes < 0) '-' else ' '
        minutes = abs(minutes)
        return "%c%02d:%02d".format(pre, minutes / 60, minutes % 60)
    }
    return ""
}

