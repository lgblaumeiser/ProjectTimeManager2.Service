// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity
import de.lgblaumeiser.ptm.service.model.Booking
import de.lgblaumeiser.ptm.service.model.User
import de.lgblaumeiser.ptm.service.store.BookingStore
import de.lgblaumeiser.ptm.service.store.Store
import java.time.LocalDate
import kotlin.reflect.KCallable
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.instanceParameter

abstract class TestStore<T> : Store<T> {
    abstract val copyFun: KCallable<T>

    protected val dataobjects = mutableListOf<T>()

    override fun retrieveAll(user: String) = dataobjects.toList().filter { username(it).equals(user, true) }

    override fun retrieveById(user: String, id: Long) = dataobjects
        .filter { id(it) == id }.firstOrNull { username(it).equals(user, true) }
        ?: throw IllegalStateException("Object with id $id not found")

    override fun create(data: T): T {
        dataobjects.filter { username(it).equals(user, true) && hasProperty(it, name, values) }

    override fun create(user: String, data: T): T {
        require(username(data).equals(user, true))
        val id = nextId()
        val toCopy = copyFun.instanceParameter!!
        val idParam = copyFun.findParameterByName("id")!!
        val objWithId = copyFun.callBy(
            mapOf(
                toCopy to data,
                idParam to id
            )
        )
        dataobjects.add(objWithId)
        return objWithId
    }

    private fun nextId() = (dataobjects.maxOfOrNull { id(it) } ?: 0L) + 1L

    override fun update(user: String, data: T) {
        require(username(data).equals(user, true))
        delete(username(data), id(data))
        dataobjects.add(data)
    }

    override fun delete(user: String, id: Long) {
        dataobjects.remove(retrieveById(user, id))
    }

    fun clear() {
        dataobjects.clear()
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

class UserTestStore : TestStore<User>() {
    override val copyFun = User::copy
}

class ActivityTestStore : TestStore<Activity>() {
    override val copyFun = Activity::copy
}

class BookingTestStore : TestStore<Booking>(), BookingStore {
    override val copyFun = Booking::copy

    override fun retrieveByBookingDays(user: String, days: List<LocalDate>): List<Booking> =
        dataobjects.filter { it.user.equals(user, true) && days.contains(it.bookingday) }
}