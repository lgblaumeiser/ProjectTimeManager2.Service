// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity
import de.lgblaumeiser.ptm.service.store.Store

class ActivityTestStore: Store<Activity> {
    val activities = mutableListOf<Activity>()

    override fun retrieveAll(user: String) = activities.toList().filter { it.user.equals(user, true) }

    override fun retrieveById(user: String, id: Long) = activities
        .filter { it.id == id }
        .filter { it.user.equals(user, true) }
        .firstOrNull()
        ?: throw IllegalStateException("Activity with id $id not found")

    override fun retrieveByProperty(user: String, name: String, values: Collection<Any>) = activities.filter { hasProperty(it, name, values) }

    private fun hasProperty(activity: Activity, name: String, values: Collection<Any>): Boolean {
        when(name) {
            "id" -> return values.contains(activity.id)
            "user" -> return values.contains(activity.user)
            "projectname" -> return values.contains(activity.projectname)
            "projectid" -> return values.contains(activity.projectid)
            "activityname" -> return values.contains(activity.activityname)
            "activityid" -> return values.contains(activity.activityid)
            "hidden" -> return values.contains(activity.hidden)
            else -> throw IllegalArgumentException("Property $name not known for Activity")
        }
    }

    override fun create(user: String, data: Activity): Activity {
        require(data.user.equals(user, true))
        val id = nextId()
        activities.add(data.copy(id = id))
        return data.copy(id = id)
    }

    private fun nextId() = ( activities.map { it.id }.max() ?: 0L ) + 1L

    override fun update(user: String, data: Activity) {
        require(data.user.equals(user, true))
        delete(data.user, data.id)
        activities.add(data)
    }

    override fun delete(user: String, id: Long) {
        activities.remove(retrieveById(user, id))
    }
}
