// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.User

const val testUsername1 = "user1"
const val testUsername2 = "user2"

const val testPassword1 = "MySecret"
const val testPassword2 = "MyOtherSecret"

val userStore = UserTestStore()
val userService = UserService(userStore, activityService, bookingService)

val testUser = User(-1, testUsername1, testPassword1)

fun initializeUserService() {
    initializeActivityService()
    initializeBookingService()
    userStore.clear()
}

fun addTestUser1() = userService.addUser(testUsername1, testPassword1)

fun addTestUser2() = userService.addUser(testUsername2, testPassword2)
