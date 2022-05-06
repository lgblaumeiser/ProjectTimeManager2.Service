// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import java.time.LocalDate
import java.time.LocalTime

class BookingTest : WordSpec({

    val testBookingDay = LocalDate.now()

    val testStarttime = LocalTime.now().minusHours(1L)

    val testEndtime = LocalTime.now()

    val testActivity = 1L

    val testBookingUser = "userid1"

    val testComment = "My test comment"

    "Booking validation" should {
        "should be fine with minimal fields" {
            val booking = Booking(
                bookingday = testBookingDay,
                starttime = testStarttime,
                activity = testActivity,
                user = testBookingUser
            )
            should {
                booking.id == -1L
                booking.bookingday == testBookingDay
                booking.starttime == testStarttime
                booking.endtime == null
                booking.activity == testActivity
                booking.user == testBookingUser
                booking.comment.isBlank()
            }
        }

        "should be fine with all fields" {
            val booking = Booking(
                bookingday = testBookingDay,
                starttime = testStarttime,
                activity = testActivity,
                user = testBookingUser,
                id = 1L,
                endtime = testEndtime,
                comment = testComment
            )
            should {
                booking.id == 1L
                booking.bookingday == testBookingDay
                booking.starttime == testStarttime
                booking.endtime?.let {it == testEndtime }
                booking.activity == testActivity
                booking.user == testBookingUser
                booking.comment == testComment
            }
        }

        "should throw exception with blank user" {
            shouldThrow<IllegalArgumentException> {
                Booking(
                    bookingday = testBookingDay,
                    starttime = testStarttime,
                    activity = testActivity,
                    user = ""
                )
            }
        }

        "should throw exception with activity id == 0" {
            shouldThrow<IllegalArgumentException> {
                Booking(
                    bookingday = testBookingDay,
                    starttime = testStarttime,
                    activity = 0L,
                    user = testBookingUser
                )
            }
        }

        "should throw exception with activity id < 0" {
            shouldThrow<IllegalArgumentException> {
                Booking(
                    bookingday = testBookingDay,
                    starttime = testStarttime,
                    activity = -1L,
                    user = testBookingUser
                )
            }
        }

        "should throw exception with endtime before starttime" {
            shouldThrow<IllegalArgumentException> {
                Booking(
                    bookingday = testBookingDay,
                    starttime = testEndtime,
                    endtime = testStarttime,
                    activity = 1L,
                    user = testBookingUser
                )
            }
        }

        "should throw exception with endtime equals to starttime" {
            shouldThrow<IllegalArgumentException> {
                Booking(
                    bookingday = testBookingDay,
                    starttime = testEndtime,
                    endtime = testEndtime,
                    activity = 1L,
                    user = testBookingUser
                )
            }
        }
    }
})
