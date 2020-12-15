package de.lgblaumeiser.ptm.service.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should

val testUsername = "username"

val testPassword = "Secret"

val testEmail = "user@domain.org"

val testWrongEmail = "user<at>domain.org"

val testQuestion = "Wer ist der Bürgermeister von Wesel?"

val testAnswer = "...Esel!"

class UserTest: WordSpec ({

    "User validation" should {
        "should be fine with minimal fields" {
            val user = User(
                username = testUsername,
                password = testPassword,
                email = testEmail,
                question = testQuestion,
                answer = testAnswer)
            should {
                user.id == -1L;
                user.username.equals(testUsername)
                user.password.equals(testPassword)
                user.email.equals(testEmail)
                user.question.equals(testQuestion)
                user.answer.equals(testAnswer)
                !user.admin
            }
        }

        "should be fine with all fields set" {
            val user = User(
                id = 1L,
                username = testUsername,
                password = testPassword,
                email = testEmail,
                question = testQuestion,
                answer = testAnswer)
            val adminUser = user.copy(admin = true)
            should {
                adminUser.id == 1L;
                adminUser.username.equals(testUsername)
                adminUser.password.equals(testPassword)
                adminUser.email.equals(testEmail)
                adminUser.question.equals(testQuestion)
                adminUser.answer.equals(testAnswer)
                adminUser.admin
            }
        }

        "should throw exception with blank username" {
            shouldThrow<IllegalArgumentException> {
                User(
                    username = "",
                    password = testPassword,
                    email = testEmail,
                    question = testQuestion,
                    answer = testAnswer)
            }
        }

        "should throw exception with blank password" {
            shouldThrow<IllegalArgumentException> {
                User(
                    username = testUsername,
                    password = "",
                    email = testEmail,
                    question = testQuestion,
                    answer = testAnswer)
            }
        }

        "should throw exception with blank email" {
            shouldThrow<IllegalArgumentException> {
                User(
                    username = testUsername,
                    password = testPassword,
                    email = "",
                    question = testQuestion,
                    answer = testAnswer)
            }
        }

        "should throw exception with wrong email" {
            shouldThrow<IllegalArgumentException> {
                User(
                    username = testUsername,
                    password = testPassword,
                    email = testWrongEmail,
                    question = testQuestion,
                    answer = testAnswer)
            }
        }

        "should throw exception with blank question" {
            shouldThrow<IllegalArgumentException> {
                User(
                    username = testUsername,
                    password = testPassword,
                    email = testEmail,
                    question = "",
                    answer = testAnswer)
            }
        }

        "should throw exception with blank answer" {
            shouldThrow<IllegalArgumentException> {
                User(
                    username = testUsername,
                    password = testPassword,
                    email = testEmail,
                    question = testQuestion,
                    answer = "")
            }
        }
    }
})
