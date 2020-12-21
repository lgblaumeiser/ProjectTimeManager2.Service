package de.lgblaumeiser.ptm.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import java.time.LocalDate
import java.time.LocalTime

class BookingServiceTest : WordSpec({

    val testBookingUser1 = "userid1"
    val testBookingUser2 = "userid2"

    val testDate1 = "2020-12-01"
    val testDate2 = "2020-12-03"

    val testTime1 = "08:00"
    val testTime2 = "09:30"
    val testTime3 = "10:00"
    val testTime4 = "12:00"

    val testActivityId1 = 1L
    val testActivityId2 = 2L

    val testComment = "Cool comment for booking"

    val testDuration = 45L

    fun initializeService() = BookingService(BookingTestStore())

    "getBookings" should {
        "return an empty collection when nothing is stored" {
            val service = initializeService()
            should { service.getBookings(testBookingUser1).shouldBeEmpty() }
        }
    }

    "getBooking with time frame" should {
        "return an empty collection when nothing is stored (only starttime)" {
            val service = initializeService()
            should { service.getBookings(testBookingUser1, testDate1) }
        }

        "return an empty collection when nothing is stored (starttime and enttime)" {
            val service = initializeService()
            should { service.getBookings(testBookingUser1, testDate1, testDate2) }
        }

        "return with exception if timeframe is defined negative" {
            val service = initializeService()
            shouldThrow<IllegalArgumentException> { service.getBookings(testBookingUser1, testDate2, testDate1) }
        }
    }

    "getBookingById" should {
        "return an exception if asked for a id that do not exist" {
            val service = initializeService()
            shouldThrow<IllegalStateException> { service.getBookingById(testBookingUser1, 1L) }
        }
    }

    "add booking" should {
        "minimal add booking stores the defined booking" {
            val service = initializeService()
            val booking = service.addBooking(
                user = testBookingUser1,
                bookingday = testDate1,
                starttime = testTime1,
                activity = testActivityId1
            )
            should {
                service.getBookings(testBookingUser1).shouldContainExactly(booking)
                service.getBookings(testBookingUser2).shouldBeEmpty()
                service.getBookings(testBookingUser1, testDate1).shouldContainExactly(booking)
                service.getBookings(testBookingUser1, testDate1, testDate2).shouldContainExactly(booking)
                service.getBookings(testBookingUser1, testDate2)
                service.getBookingById(testBookingUser1, 1L) == booking
                shouldThrow<IllegalStateException> { service.getBookingById(testBookingUser2, 1L) }
                shouldThrow<IllegalStateException> { service.getBookingById(testBookingUser1, 2L) }
            }
        }

        "maximal add booking stores the defined booking" {
            val service = initializeService()
            val booking =
                service.addBooking(testBookingUser1, testDate1, testTime1, testTime2, testActivityId1, testComment)
            should {
                service.getBookings(testBookingUser1).shouldContainExactly(booking)
                val retrieved = service.getBookingById(testBookingUser1, 1L)
                retrieved.user == testBookingUser1
                retrieved.activity == testActivityId1
                retrieved.bookingday == LocalDate.parse(testDate1)
                retrieved.starttime == LocalTime.parse(testTime1)
                retrieved.endtime == LocalTime.parse(testTime2)
                retrieved.comment == testComment
            }
        }

        "an open booking is closed when new booking arrives" {
            val service = initializeService()
            val booking1 = service.addBooking(
                user = testBookingUser1,
                bookingday = testDate1,
                starttime = testTime1,
                activity = testActivityId1
            )
            val booking2 =
                service.addBooking(testBookingUser1, testDate1, testTime2, testTime3, testActivityId2, testComment)
            should {
                service.getBookingById(testBookingUser1, 1L) == booking1.copy(endtime = LocalTime.parse(testTime2))
                service.getBookingById(testBookingUser1, 2L) == booking2
                service.getBookings(testBookingUser1, testDate1)[1].equals(booking2)
            }
        }

        "two bookings stored by different users can only be retrieved by corresponding user" {
            val service = initializeService()
            val booking1 = service.addBooking(
                user = testBookingUser1,
                bookingday = testDate1,
                starttime = testTime1,
                activity = testActivityId1
            )
            val booking2 =
                service.addBooking(testBookingUser2, testDate1, testTime2, testTime3, testActivityId2, testComment)
            should {
                service.getBookings(testBookingUser1).shouldContainExactly(booking1)
                service.getBookings(testBookingUser2).shouldContainExactly(booking2)
            }
        }
    }

    "change booking" should {
        "changing an existing booking result in a data object with the changed values" {
            val service = initializeService()
            val booking = service.addBooking(
                user = testBookingUser1,
                bookingday = testDate1,
                starttime = testTime1,
                activity = testActivityId1
            )
            service.changeBooking(
                user = testBookingUser1,
                bookingday = testDate2,
                endtime = testTime4,
                comment = testComment,
                id = booking.id
            )
            val changed = service.getBookingById(testBookingUser1, booking.id)
            should {
                changed.user == testBookingUser1
                changed.activity == testActivityId1
                changed.bookingday == LocalDate.parse(testDate2)
                changed.starttime == LocalTime.parse(testTime1)
                changed.endtime == LocalTime.parse(testTime4)
                changed.comment == testComment
            }
        }

        "changing an existing booking not possible for different user" {
            val service = initializeService()
            val booking = service.addBooking(
                user = testBookingUser1,
                bookingday = testDate1,
                starttime = testTime1,
                activity = testActivityId1
            )
            shouldThrow<IllegalStateException> {
                service.changeBooking(
                    user = testBookingUser2,
                    bookingday = testDate2,
                    endtime = testTime4,
                    comment = testComment,
                    id = booking.id
                )
            }
        }
    }

    "split booking" should {
        "should create a proper second booking if parameters are right" {
            val service = initializeService()
            val booking =
                service.addBooking(testBookingUser1, testDate1, testTime1, testTime4, testActivityId1, testComment)
            val splited = service.splitBooking(testBookingUser1, testTime2, testDuration, booking.id)
            should {
                val first = service.getBookingById(testBookingUser1, 1L)
                val second = service.getBookingById(testBookingUser1, 2L)
                second == splited
                first.user == testBookingUser1
                second.user == testBookingUser1
                first.bookingday == LocalDate.parse(testDate1)
                second.bookingday == LocalDate.parse(testDate1)
                first.starttime == LocalTime.parse(testTime1)
                first.endtime == LocalTime.parse(testTime2)
                second.starttime == LocalTime.parse(testTime2).plusMinutes(testDuration)
                second.endtime == LocalTime.parse(testTime4)
                first.activity == testActivityId1
                second.activity == testActivityId2
                first.comment == testComment
                second.comment == testComment
            }
        }

        "should create a proper second booking if parameters are right with default duration" {
            val service = initializeService()
            val booking =
                service.addBooking(testBookingUser1, testDate1, testTime1, testTime4, testActivityId1, testComment)
            val splited = service.splitBooking(user = testBookingUser1, starttime = testTime2, id = booking.id)
            should {
                val first = service.getBookingById(testBookingUser1, 1L)
                val second = service.getBookingById(testBookingUser1, 2L)
                second == splited
                first.user == testBookingUser1
                second.user == testBookingUser1
                first.bookingday == LocalDate.parse(testDate1)
                second.bookingday == LocalDate.parse(testDate1)
                first.starttime == LocalTime.parse(testTime1)
                first.endtime == LocalTime.parse(testTime2)
                second.starttime == LocalTime.parse(testTime2).plusMinutes(30L)
                second.endtime == LocalTime.parse(testTime4)
                first.activity == testActivityId1
                second.activity == testActivityId2
                first.comment == testComment
                second.comment == testComment
            }
        }

        "throws exception if wrong user is given" {
            val service = initializeService()
            val booking =
                service.addBooking(testBookingUser1, testDate1, testTime1, testTime4, testActivityId1, testComment)
            shouldThrow<IllegalStateException> {
                service.splitBooking(
                    testBookingUser2,
                    testTime2,
                    testDuration,
                    booking.id
                )
            }
        }

        "throws exception if split time is not in time frame" {
            val service = initializeService()
            val booking =
                service.addBooking(testBookingUser1, testDate1, testTime1, testTime2, testActivityId1, testComment)
            shouldThrow<IllegalArgumentException> {
                service.splitBooking(
                    testBookingUser1,
                    testTime3,
                    testDuration,
                    booking.id
                )
            }
        }

        "throws exception if duration is longer then endtime" {
            val service = initializeService()
            val booking =
                service.addBooking(testBookingUser1, testDate1, testTime1, testTime3, testActivityId1, testComment)
            shouldThrow<IllegalArgumentException> {
                service.splitBooking(
                    testBookingUser1,
                    testTime2,
                    testDuration,
                    booking.id
                )
            }
        }
    }

    "delete booking" should {
        "removes booking if properties match" {
            val service = initializeService()
            val booking =
                service.addBooking(testBookingUser1, testDate1, testTime1, testTime4, testActivityId1, testComment)
            should { service.getBookingById(testBookingUser1, 1L).equals(booking) }
            service.deleteBooking(testBookingUser1, 1L)
            shouldThrow<IllegalStateException> { service.getBookingById(testBookingUser1, 1L) }
        }

        "throws access exception if booking is deleted by wrong user" {
            val service = initializeService()
            val booking =
                service.addBooking(testBookingUser1, testDate1, testTime1, testTime4, testActivityId1, testComment)
            should { service.getBookingById(testBookingUser1, 1L).equals(booking) }
            shouldThrow<java.lang.IllegalStateException> {
                service.deleteBooking(testBookingUser2, 1L)
            }
        }
    }
})