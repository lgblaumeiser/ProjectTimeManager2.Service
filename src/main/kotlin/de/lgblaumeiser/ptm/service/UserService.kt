// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.User
import de.lgblaumeiser.ptm.service.store.Store
import java.util.UUID

class UserService(val store: Store<User>, val activities: ActivityService, val bookings: BookingService) {
    fun addUser(
        username: String,
        password: String,
        email: String,
        question: String,
        answer: String
    ): Long {
        require(retrieveUserRecord(username) == null) { "A user with username $username already exists" }
        val id = store.create(
            User(
                username = username,
                password = password,
                email = email,
                question = question,
                answer = answer
            )
        )
        if (id == 1L) // First user gets admin rights
            store.create(store.retrieveById(id).copy(admin = true))
        return id
    }

    fun resetPassword(username: String, answer: String): String {
        val user = retrieveExistingUserRecord(username)
        if (!answer.equals(user.answer)) {
            throw IllegalAccessException("Given answer does not match stored answer, given: $answer")
        }
        val newPassword = UUID.randomUUID().toString();
        store.update(user.copy(password=newPassword))
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
    ) {
        val user = retrieveExistingUserRecord(username)
        val newPassword = password ?: user.password
        val newEmail = email ?: user.email
        val newQuestion = question ?: user.question
        val newAnswer = answer ?: user.answer
        store.update(
            User(
                id = user.id,
                username = user.username,
                password = newPassword,
                email = newEmail,
                question = newQuestion,
                answer = newAnswer
            )
        )
    }

    fun deleteUser(username: String) {
        activities.getActivities(username).forEach { activities.deleteActivity(username, it.id) }
        bookings.getBookings(username).forEach { bookings.deleteBooking(username, it.id) }
        retrieveExistingUserRecord(username).let { store.delete(it.id) }
    }

    private fun retrieveUserRecord(user: String) =
        store.retrieveByProperty("username", listOf(user)).firstOrNull()

    private fun retrieveExistingUserRecord(user: String) =
        retrieveUserRecord(user) ?: throw NotFoundException("User $user not found in database")
}