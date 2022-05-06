// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should

class UserServiceTest : WordSpec({

    beforeTest {
        initializeUserService()
    }

    "get user by name" should {
        "throw exception is user is unknown" {
            shouldThrow<IllegalArgumentException> { userService.getUser(testUsername1, testPassword1) }
        }
    }

    "add user" should {
        "First added user can be retrieved, he is admin" {
            addTestUser1()
            val stored = userService.getUser(testUsername1, testPassword1)
            should {
                stored.username == testUsername1
                stored.admin
                userService.authenticateUser(testUsername1, testPassword1)
            }
        }

        "First added is admin and second is not" {
            addTestUser1()
            addTestUser2()
            should {
                userService.getUser(testUsername1, testPassword1).admin
                !userService.getUser(testUsername2, testPassword2).admin
            }
        }

        "No two users with same username allowed" {
            addTestUser1()
            shouldThrow<IllegalArgumentException> {
                userService.addUser(testUsername1, testPassword2, testEmail2, testQuestion2, testAnswer2)
            }
        }

        "add user sets password, so that user cannot be authenticated with wrong password" {
            addTestUser1()
            should {
                userService.authenticateUser(testUsername1, testPassword1)
                !userService.authenticateUser(testUsername1, testPassword2)
            }
            shouldThrow<IllegalAccessException> { userService.getUser(testUsername1, testPassword2) }
        }
    }

    "change user" should {
        "It is possible to change all properties except username" {
            addTestUser1()
            should {
                userService.authenticateUser(testUsername1, testPassword1)
                !userService.authenticateUser(testUsername1, testPassword2)
            }
            userService.changePassword(
                username = testUsername1,
                newPassword = testPassword2
            )
            should {
                userService.authenticateUser(testUsername1, testPassword2)
                !userService.authenticateUser(testUsername1, testPassword1)
            }
        }
    }

    "delete user" should {
        "delete user deletes all activities and bookings before user is deleted" {
            addTestUser1()
            addStandardActivity1()
            addCompleteBooking()
            should { userService.authenticateUser(testUsername1, testPassword1) }
            userService.deleteUser(testUsername1, testPassword1)
            should {
                activityService.getActivities(testUsername1, true).isEmpty()
                bookingService.getBookings(testUsername1).isEmpty()
            }
            shouldThrow<java.lang.IllegalArgumentException> { userService.getUser(testUsername1, testPassword1) }
        }

        "delete user should not work with wrong password" {
            addTestUser1()
            should { userService.authenticateUser(testUsername1, testPassword1) }
            shouldThrow<IllegalAccessException> {
                userService.deleteUser(testUsername1, testPassword2)
            }
        }
    }
})
