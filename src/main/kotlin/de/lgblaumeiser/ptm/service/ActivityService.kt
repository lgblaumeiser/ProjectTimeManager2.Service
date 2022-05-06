// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity
import de.lgblaumeiser.ptm.service.store.Store

open class ActivityService(val store: Store<Activity>) {
    fun getActivities(user: String, hidden: Boolean = false) = store
        .retrieveAll(user)
        .filter { hidden || !it.hidden }
        .sortedWith(compareBy<Activity> { it.projectid.uppercase() }.thenBy { it.activityid.uppercase() })

    fun getActivityById(user: String, id: Long): Activity = store.retrieveById(user, id)

    fun addActivity(
        user: String,
        projectname: String,
        projectid: String,
        activityname: String,
        activityid: String
    ) = store.create(
        Activity(
            user = user,
            projectname = projectname,
            projectid = projectid,
            activityname = activityname,
            activityid = activityid
        )
    )

    fun changeActivity(
        user: String,
        projectname: String? = null,
        projectid: String? = null,
        activityname: String? = null,
        activityid: String? = null,
        hidden: Boolean? = null,
        id: Long
    ) = getActivityById(user, id).let {
        store.update(
            Activity(
                id = it.id,
                user = it.user,
                projectname = projectname ?: it.projectname,
                projectid = projectid ?: it.projectid,
                activityname = activityname ?: it.activityname,
                activityid = activityid ?: it.activityid,
                hidden = hidden ?: it.hidden
            )
        )
    }

    fun deleteActivity(user: String, id: Long) =
        getActivityById(user, id).let { store.delete(id) }
}
