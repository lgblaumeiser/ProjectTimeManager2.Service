// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
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
    ) {
        require(retrieveUserRecord(username) == null) { "A user with username $username already exists" }
        val user = store.create(
            User(
                username = username,
                password = encrypt(password),
            )
        )
        if (user.id == 1L) // First user gets admin rights
            store.update(user.copy(admin = true))
    }

    fun authenticateUser(username: String, password: String) =
        BCrypt.verifyer().verify(password.toCharArray(), retrieveExistingUserRecord(username).password).verified

    fun changePassword(
        username: String,
        newPassword: String
    ) = retrieveExistingUserRecord(username).let {
        store.update(
            User(
                id = it.id,
                username = it.username,
                password = encrypt(newPassword)
            )
        )
    }

    fun deleteUser(username: String) {
        activities.getActivities(username).forEach { activities.deleteActivity(username, it.id) }
        bookings.getBookings(username).forEach { bookings.deleteBooking(username, it.id) }
        retrieveExistingUserRecord(username).let { store.delete(it.id) }
    }

    internal fun retrieveUserRecord(user: String) = store.retrieveAll(user).firstOrNull()

    internal fun retrieveExistingUserRecord(user: String) =
        retrieveUserRecord(user) ?: throw IllegalArgumentException("User $user not found in database")

    private fun encrypt(password: String) =
        BCrypt.withDefaults().hashToString(12, password.toCharArray()) ?: ""
}