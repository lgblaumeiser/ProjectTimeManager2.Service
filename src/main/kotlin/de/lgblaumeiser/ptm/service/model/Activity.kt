// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

data class Activity (
    val id: Long = -1,
    val user: String,
    val projectname: String,
    val projectid: String,
    val activityname: String,
    val activityid: String,
    val hidden: Boolean = false
) {
    init {
        require(user.isNotBlank()) { "user field must not be empty for activity" }
        require(projectname.isNotBlank()) { "projectName field must not be empty for activity" }
        require(projectid.isNotBlank()) { "projectId field must not be empty for activity" }
        require(activityname.isNotBlank()) { "activityName field must not be empty for activity" }
        require(activityid.isNotBlank()) { "activityId field must not be empty for activity" }
    }
}