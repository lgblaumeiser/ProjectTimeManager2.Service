// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity
import de.lgblaumeiser.ptm.service.store.Store

class ActivityService(val store: Store<Activity>) {
    fun getActivities(user: String) = store
        .retrieveAll()
        .filter { sameUser(it.user, user) }
        .sortedWith(compareBy<Activity> { it.projectid.toUpperCase() }.thenBy { it.activityid.toUpperCase() })

    fun getActivityById(user: String, id: Long): Activity = store
        .retrieveById(id)
        .ownedByUserOrException(user)

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

fun Activity.ownedByUserOrException(requester: String): Activity {
    if(differentUser(this.user, requester)) {
        throw UserAccessException("User mismatch, resource does not belong to $requester")
    }
    return this
}
