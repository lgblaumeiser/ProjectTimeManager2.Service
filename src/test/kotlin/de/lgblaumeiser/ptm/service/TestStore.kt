// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity
import de.lgblaumeiser.ptm.service.model.Booking
import de.lgblaumeiser.ptm.service.model.User
import de.lgblaumeiser.ptm.service.store.Store
import kotlin.reflect.KCallable
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.instanceParameter

abstract class TestStore<T> : Store<T> {
    abstract val copyFun: KCallable<T>

    private val dataobjects = mutableListOf<T>()

    override fun retrieveAll(user: String) = dataobjects.toList().filter { username(it).equals(user, true) }

    override fun retrieveById(user: String, id: Long) = dataobjects
        .filter { id(it) == id }.firstOrNull { username(it).equals(user, true) }
        ?: throw IllegalStateException("Activity with id $id not found")

    override fun retrieveByProperty(user: String, name: String, values: Collection<Any>) =
        dataobjects.filter { hasProperty(it, name, values) }

    override fun create(user: String, data: T): T {
        require(username(data).equals(user, true))
        val id = nextId()
        val toCopy = copyFun.instanceParameter!!
        val idParam = copyFun.findParameterByName("id")!!
        val objWithId = copyFun.callBy( mapOf (
            toCopy to data,
            idParam to id
        ))
        dataobjects.add(objWithId)
        return objWithId
    }

    private fun nextId() = (dataobjects.map { id(it) }.max() ?: 0L) + 1L

    override fun update(user: String, data: T) {
        require(username(data).equals(user, true))
        delete(username(data), id(data))
        dataobjects.add(data)
    }

    override fun delete(user: String, id: Long) {
        dataobjects.remove(retrieveById(user, id))
    }
}

private fun <T> id(obj: T): Long {
    when (obj) {
        is User -> return obj.id
        is Activity -> return obj.id
        is Booking -> return obj.id
    }
    throw IllegalStateException("Unknown object type")
}

private fun <T> username(obj: T): String {
    when (obj) {
        is User -> return obj.username
        is Activity -> return obj.user
        is Booking -> return obj.user
    }
    throw IllegalStateException("Unknown object type")
}

private fun <T> hasProperty(obj: T, name: String, values: Collection<Any>): Boolean {
    when (obj) {
        is User -> return userHasProperty(obj, name, values)
        is Activity -> return false
        is Booking -> return bookingHasProperty(obj, name, values)
    }
    return false
}

private fun userHasProperty(user: User, name: String, values: Collection<Any>): Boolean {
    when (name) {
        "username" -> return values.contains(user.username)
    }
    return false
}

private fun bookingHasProperty(booking: Booking, name: String, values: Collection<Any>) =
    when (name) {
        "bookingday" -> values.contains(booking.bookingday)
        else -> false
    }

class UserTestStore: TestStore<User>() {
    override val copyFun = User::copy
}

class ActivityTestStore: TestStore<Activity>() {
    override val copyFun = Activity::copy
}

class BookingTestStore: TestStore<Booking>() {
    override val copyFun = Booking::copy
}