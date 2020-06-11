// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

data class Activity (
    val id: Long = -1,
    val user: String,
    val projectName: String,
    val projectId: String,
    val activityName: String,
    val activityId: String,
    val hidden: Boolean = false)