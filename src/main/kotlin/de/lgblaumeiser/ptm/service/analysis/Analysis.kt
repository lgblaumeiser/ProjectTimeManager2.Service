// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

interface Analysis<T> {
    fun analyze(username: String, firstDay: String, firstDayAfter: String) : List<T>
}