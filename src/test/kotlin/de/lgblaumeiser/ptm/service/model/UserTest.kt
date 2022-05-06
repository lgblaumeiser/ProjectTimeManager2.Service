// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should

class UserTest : WordSpec({

    val testUsername = "username"

    val testPassword = "Secret"

    "User validation" should {
        "should be fine for normal user" {
            val user = User(
                username = testUsername,
                password = testPassword,
            )
            should {
                user.id == -1L
                user.username.equals(testUsername)
                user.password.equals(testPassword)
                !user.admin
            }
        }

        "should be fine for admin user" {
            val user = User(
                id = 1L,
                username = testUsername,
                password = testPassword,
            )
            val adminUser = user.copy(admin = true)
            should {
                adminUser.id == 1L
                adminUser.username.equals(testUsername)
                adminUser.password.equals(testPassword)
                adminUser.admin
            }
        }

        "should throw exception with blank username" {
            shouldThrow<IllegalArgumentException> {
                User(
                    username = "",
                    password = testPassword,
                )
            }
        }

        "should throw exception with blank password" {
            shouldThrow<IllegalArgumentException> {
                User(
                    username = testUsername,
                    password = "",
                )
            }
        }
    }
})
