// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.User

const val testUsername1 = "user1"
const val testUsername2 = "user2"

const val testPassword1 = "MySecret"
const val testPassword2 = "MyOtherSecret"

const val testEmail1 = "me@somewhere.org"
const val testEmail2 = "me@somewhere_else.org"

const val testQuestion1 = "Hugo"
const val testQuestion2 = "Sylt"

const val testAnswer1 = "Aperol Spritz"
const val testAnswer2 = "Rügen"

val userStore = UserTestStore()
val userService = UserService(userStore, activityService, bookingService)

val testUser = User(-1, testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)

fun initializeUserService() {
    initializeActivityService()
    initializeBookingService()
    userStore.clear()
}

fun addTestUser1() = userService.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)

fun addTestUser2() = userService.addUser(testUsername2, testPassword2, testEmail2, testQuestion2, testAnswer2)
