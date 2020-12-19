// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import at.favre.lib.crypto.bcrypt.BCrypt
import de.lgblaumeiser.ptm.service.model.User
import de.lgblaumeiser.ptm.service.store.Store
import java.util.*

class UserService(
    private val store: Store<User>,
    private val activities: ActivityService,
    private val bookings: BookingService
) {
    fun addUser(
        username: String,
        password: String,
        email: String,
        question: String,
        answer: String
    ) {
        require(retrieveUserRecord(username) == null) { "A user with username $username already exists" }
        val user = store.create(
            username,
            User(
                username = username,
                password = encrypt(password),
                email = email,
                question = question,
                answer = encrypt(answer)
            )
        )
        if (user.id == 1L) // First user gets admin rights
            store.update(username, user.copy(admin = true))
    }

    fun resetPassword(username: String, answer: String): String {
        val user = retrieveExistingUserRecord(username)
        if (!BCrypt.verifyer().verify(answer.toCharArray(), user.answer).verified) {
            throw IllegalAccessException("Given answer does not match stored answer, given: $answer")
        }
        val newPassword = UUID.randomUUID().toString()
        store.update(username, user.copy(password = encrypt(newPassword)))
        return newPassword
    }

    fun getUser(username: String, password: String) =
        if (authenticateUser(username, password))
            retrieveExistingUserRecord(username).copy(password = "xxx", answer = "xxx")
        else
            throw IllegalAccessException("User not authenticated")

    fun authenticateUser(username: String, password: String) =
        BCrypt.verifyer().verify(password.toCharArray(), retrieveExistingUserRecord(username).password).verified

    fun changeUser(
        username: String,
        password: String,
        newPassword: String? = null,
        email: String? = null,
        question: String? = null,
        answer: String? = null
    ) = retrieveExistingUserRecord(username).let {
        if (authenticateUser(username, password)) {
            store.update(
                username,
                User(
                    id = it.id,
                    username = it.username,
                    password = newPassword?.let { encrypt(newPassword) } ?: it.password,
                    email = email ?: it.email,
                    question = question ?: it.question,
                    answer = answer?.let { encrypt(answer) } ?: it.answer
                )
            )
        } else throw IllegalAccessException("User not authenticated")
    }

    fun deleteUser(username: String, password: String) {
        if (authenticateUser(username, password)) {
            activities.getActivities(username).forEach { activities.deleteActivity(username, it.id) }
            bookings.getBookings(username).forEach { bookings.deleteBooking(username, it.id) }
            retrieveExistingUserRecord(username).let { store.delete(username, it.id) }
        } else
            throw IllegalAccessException("User not authenticated")
    }

    private fun retrieveUserRecord(user: String) =
        store.retrieveByProperty(user, "username", listOf(user)).firstOrNull()

    private fun retrieveExistingUserRecord(user: String) =
        retrieveUserRecord(user) ?: throw IllegalArgumentException("User $user not found in database")

    private fun encrypt(password: String) =
        BCrypt.withDefaults().hashToString(12, password.toCharArray()) ?: ""
}