// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.User
import de.lgblaumeiser.ptm.service.store.Store
import java.lang.IllegalStateException
import java.util.UUID

class UserService(val store: Store<User>, val activities: ActivityService, val bookings: BookingService) {
    fun addUser(
        username: String,
        password: String,
        email: String,
        question: String,
        answer: String
    ): User {
        require(retrieveUserRecord(username) == null) { "A user with username $username already exists" }
        val user = store.create(
            username,
            User(
                username = username,
                password = password,
                email = email,
                question = question,
                answer = answer
            )
        )
        if (user.id == 1L) // First user gets admin rights
            store.update(username, user.copy(admin = true))
        return user
    }

    fun resetPassword(username: String, answer: String): String {
        val user = retrieveExistingUserRecord(username)
        if (!answer.equals(user.answer)) {
            throw IllegalAccessException("Given answer does not match stored answer, given: $answer")
        }
        val newPassword = UUID.randomUUID().toString();
        store.update(username, user.copy(password=newPassword))
        return newPassword
    }

    fun getUser(username: String) =
        retrieveExistingUserRecord(username).copy(password = "xxx", answer = "xxx")

    fun changeUser(
        username: String,
        password: String? = null,
        email: String? = null,
        question: String? = null,
        answer: String? = null
    ) = retrieveExistingUserRecord(username).let {
        store.update(
            username,
            User(
                id = it.id,
                username = it.username,
                password = password ?: it.password,
                email = email ?: it.email,
                question = question ?: it.question,
                answer = answer ?: it.answer
            )
        )
    }

    fun deleteUser(username: String) {
        activities.getActivities(username).forEach { activities.deleteActivity(username, it.id) }
        bookings.getBookings(username).forEach { bookings.deleteBooking(username, it.id) }
        retrieveExistingUserRecord(username).let { store.delete(username, it.id) }
    }

    private fun retrieveUserRecord(user: String) =
        store.retrieveByProperty(user, "username", listOf(user)).firstOrNull()

    private fun retrieveExistingUserRecord(user: String) =
        retrieveUserRecord(user) ?: throw IllegalStateException("User $user not found in database")
}