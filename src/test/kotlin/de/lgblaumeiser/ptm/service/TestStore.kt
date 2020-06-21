// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity
import de.lgblaumeiser.ptm.service.store.Store

class ActivityTestStore: Store<Activity> {
    val activities = mutableListOf<Activity>()

    override fun retrieveAll() = activities.toList()

    override fun retrieveById(id: Long) = activities.filter { it.id == id }.firstOrNull()
        ?: throw NotFoundException("Activity with id $id not found")

    override fun retrieveByProperty(name: String, values: Collection<Any>) = activities.filter { hasProperty(it, name, values) }

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

    override fun create(data: Activity): Long {
        val id = nextId()
        activities.add(data.copy(id = id))
        return id
    }

    private fun nextId() = activities.map { it.id }.max() ?: 0L + 1L

    override fun update(data: Activity) {
        delete(data.id)
        activities.add(data)
    }

    override fun delete(id: Long) {
        activities.remove(retrieveById(id))
    }
}
