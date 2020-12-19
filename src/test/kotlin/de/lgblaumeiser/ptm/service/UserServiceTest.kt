package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity
import de.lgblaumeiser.ptm.service.model.Booking
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import java.time.LocalDate
import java.time.LocalTime

val testUsername1 = "user1"
val testUsername2 = "user2"

val testPassword1 = "MySecret"
val testPassword2 = "MyOtherSecret"

val testEmail1 = "me@somewhere.org"
val testEmail2 = "me@somewhere_else.org"

val testQuestion1 = "Hugo"
val testQuestion2 = "Sylt"

val testAnswer1 = "Aperol Spritz"
val testAnswer2 = "Rügen"

val activityService = ActivityService(ActivityTestStore())
val bookingService = BookingService(BookingTestStore())

val someString = "Egal"

class UserServiceTest : WordSpec({

    fun initializeService() = UserService(UserTestStore(), activityService, bookingService)

    "get user by name results in exception is user is unknown" should {
        val service = initializeService()
        shouldThrow<IllegalArgumentException> { service.getUser(testUsername1, testPassword1) }
    }

    "add user" should {
        "First added user can be retrieved, he is admin" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            val stored = service.getUser(testUsername1, testPassword1)
            should {
                stored.username.equals(testUsername1)
                stored.password.equals("xxx")
                stored.email.equals(testEmail1)
                stored.question.equals(testQuestion1)
                stored.answer.equals("xxx")
                stored.admin
                service.authenticateUser(testUsername1, testPassword1)
            }
        }

        "First added is admin and second is not" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            service.addUser(testUsername2, testPassword2, testEmail2, testQuestion2, testAnswer2)
            should {
                service.getUser(testUsername1, testPassword1).admin
                !service.getUser(testUsername2, testPassword2).admin
            }
        }

        "No two users with same username allowed" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            shouldThrow<IllegalArgumentException> {
                service.addUser(testUsername1, testPassword2, testEmail2, testQuestion2, testAnswer2)
            }
        }

        "add user sets password, so that user cannot be authenticated with wrong password" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            should {
                service.authenticateUser(testUsername1, testPassword1)
                !service.authenticateUser(testUsername1, testPassword2)
            }
            shouldThrow<IllegalAccessException> { service.getUser(testUsername1, testPassword2) }
        }
    }

    "change user" should {
        "It is possible to change all properties except username" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            should {
                service.authenticateUser(testUsername1, testPassword1)
                !service.authenticateUser(testUsername1, testPassword2)
            }
            service.changeUser(
                username = testUsername1,
                password = testPassword1,
                newPassword = testPassword2,
                email = testEmail2,
                question = testQuestion2,
                answer = testAnswer2
            )
            should {
                service.authenticateUser(testUsername1, testPassword2)
                !service.authenticateUser(testUsername1, testPassword1)
                val user = service.getUser(testUsername1, testPassword2)
                user.email.equals(testEmail2)
                user.question.equals(testQuestion2)
            }
        }

        "It is possible to only change the email" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            service.changeUser(
                username = testUsername1,
                password = testPassword1,
                email = testEmail2
            )
            should {
                service.authenticateUser(testUsername1, testPassword1)
                !service.authenticateUser(testUsername1, testPassword2)
                val user = service.getUser(testUsername1, testPassword1)
                user.email.equals(testEmail2)
                user.question.equals(testQuestion1)
            }
        }

        "Not possible to change user with wrong password" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            shouldThrow<IllegalAccessException> {
                service.changeUser(
                    username = testUsername1,
                    password = testPassword2,
                    email = testEmail2
                )
            }
        }
    }

    "reset password" should {
        "reset password works with right answer even after change" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            should { service.authenticateUser(testUsername1, testPassword2) }
            val newPassword = service.resetPassword(testUsername1, testAnswer1)
            should { service.authenticateUser(testUsername1, newPassword) }
            service.changeUser(
                username = testUsername1,
                password = newPassword,
                question = testQuestion2,
                answer = testAnswer2
            )
            should { service.authenticateUser(testUsername1, newPassword) }
            val secondPassword = service.resetPassword(testUsername1, testAnswer2)
            should { service.authenticateUser(testUsername1, secondPassword) }
        }

        "reset requires right answer for change" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            shouldThrow<IllegalAccessException> { service.resetPassword(testUsername1, testAnswer2) }
        }
    }

    "delete user" should {
        "delete user deletes all activities and bookings before user is deleted" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            val activity = activityService.addActivity (testUsername1, someString, someString, someString, someString)
            bookingService.addBooking( user = testUsername1, bookingday ="2020-12-20", starttime = "10:45", activity = activity.id )
            should { service.authenticateUser(testUsername1, testPassword1) }
            service.deleteUser(testUsername1, testPassword1)
            should {
                activityService.getActivities(testUsername1, true).isEmpty()
                bookingService.getBookings(testUsername1).isEmpty()
            }
            shouldThrow<java.lang.IllegalArgumentException> { service.getUser(testUsername1, testPassword1) }
        }

        "delete user should not work with wrong password" {
            val service = initializeService()
            service.addUser(testUsername1, testPassword1, testEmail1, testQuestion1, testAnswer1)
            should { service.authenticateUser(testUsername1, testPassword1) }
            shouldThrow<IllegalAccessException> {
                service.deleteUser(testUsername1, testPassword2)
            }
        }
    }
})
