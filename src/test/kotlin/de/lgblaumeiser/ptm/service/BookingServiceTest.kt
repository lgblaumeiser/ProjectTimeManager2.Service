package de.lgblaumeiser.ptm.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import java.time.LocalDate
import java.time.LocalTime

class BookingServiceTest : WordSpec({

    beforeTest {
        initializeBookingService()
    }

    "getBookings" should {
        "return an empty collection when nothing is stored" {
            should { bookingService.getBookings(testBookingUser1).shouldBeEmpty() }
        }
    }

    "getBooking with time frame" should {
        "return an empty collection when nothing is stored (only starttime)" {
            should { bookingService.getBookings(testBookingUser1, testDate1) }
        }

        "return an empty collection when nothing is stored (starttime and enttime)" {
            should { bookingService.getBookings(testBookingUser1, testDate1, testDate2) }
        }

        "return with exception if timeframe is defined negative" {
            shouldThrow<IllegalArgumentException> { bookingService.getBookings(testBookingUser1, testDate2, testDate1) }
        }
    }

    "getBookingById" should {
        "return an exception if asked for a id that do not exist" {
            shouldThrow<IllegalStateException> { bookingService.getBookingById(testBookingUser1, 1L) }
        }
    }

    "add booking" should {
        "minimal add booking stores the defined booking" {
            val booking = addMinimalBooking()
            should {
                bookingService.getBookings(testBookingUser1).shouldContainExactly(testBooking1.copy( id = booking.id ))
                bookingService.getBookings(testBookingUser2).shouldBeEmpty()
                bookingService.getBookings(testBookingUser1, testDate1).shouldContainExactly(booking)
                bookingService.getBookings(testBookingUser1, testDate1, testDate2).shouldContainExactly(booking)
                bookingService.getBookings(testBookingUser1, testDate2)
                bookingService.getBookingById(testBookingUser1, 1L) == booking
                shouldThrow<IllegalStateException> { bookingService.getBookingById(testBookingUser2, 1L) }
                shouldThrow<IllegalStateException> { bookingService.getBookingById(testBookingUser1, 2L) }
            }
        }

        "maximal add booking stores the defined booking" {
            val booking = addCompleteBooking()
            should {
                bookingService.getBookings(testBookingUser1).shouldContainExactly(testBooking2.copy( id = booking.id ))
                val retrieved = bookingService.getBookingById(testBookingUser1, 1L)
                retrieved.equals(testBooking2.copy( id = booking.id ))
            }
        }

        "an open booking is closed when new booking arrives" {
            val booking1 = addMinimalBooking()
            val booking2 = addCompleteBooking()
            should {
                bookingService.getBookingById(testBookingUser1, 1L) == testBooking1.copy( id = booking1.id, endtime = LocalTime.parse(testTime2) )
                bookingService.getBookingById(testBookingUser1, 2L) == testBooking2.copy( id = booking2.id )
                bookingService.getBookings(testBookingUser1, testDate1)[1] == booking2
            }
        }

        "two bookings stored by different users can only be retrieved by corresponding user" {
            val booking1 = addMinimalBooking()
            val booking2 =
                bookingService.addBooking(testBookingUser2, testDate1, testTime2, testTime3, testActivityId2, testComment)
            should {
                bookingService.getBookings(testBookingUser1).shouldContainExactly(booking1)
                bookingService.getBookings(testBookingUser2).shouldContainExactly(booking2)
            }
        }
    }

    "change booking" should {
        "changing an existing booking result in a data object with the changed values" {
            val booking = addMinimalBooking()
            bookingService.changeBooking(
                user = testBookingUser1,
                bookingday = testDate2,
                endtime = testTime4,
                comment = testComment,
                id = booking.id
            )
            val changed = bookingService.getBookingById(testBookingUser1, booking.id)
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
            val booking = addMinimalBooking()
            shouldThrow<IllegalStateException> {
                bookingService.changeBooking(
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
            val booking = addLongBooking()
            val splited = bookingService.splitBooking(testBookingUser1, testTime2, testDuration, booking.id)
            should {
                val first = bookingService.getBookingById(testBookingUser1, 1L)
                val second = bookingService.getBookingById(testBookingUser1, 2L)
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
            val booking = addLongBooking()
            val splited = bookingService.splitBooking(user = testBookingUser1, starttime = testTime2, id = booking.id)
            should {
                val first = bookingService.getBookingById(testBookingUser1, 1L)
                val second = bookingService.getBookingById(testBookingUser1, 2L)
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
            val booking = addLongBooking()
            shouldThrow<IllegalStateException> {
                bookingService.splitBooking(
                    testBookingUser2,
                    testTime2,
                    testDuration,
                    booking.id
                )
            }
        }

        "throws exception if split time is not in time frame" {
            val booking = addCompleteBooking()
            shouldThrow<IllegalArgumentException> {
                bookingService.splitBooking(
                    testBookingUser1,
                    testTime1,
                    testDuration,
                    booking.id
                )
            }
        }

        "throws exception if duration is longer then endtime" {
            val booking =
                bookingService.addBooking(testBookingUser1, testDate1, testTime1, testTime3, testActivityId1, testComment)
            shouldThrow<IllegalArgumentException> {
                bookingService.splitBooking(
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
            val booking = addLongBooking()
            should { bookingService.getBookingById(testBookingUser1, 1L).equals(booking) }
            bookingService.deleteBooking(testBookingUser1, booking.id)
            shouldThrow<IllegalStateException> { bookingService.getBookingById(testBookingUser1, booking.id) }
        }

        "throws access exception if booking is deleted by wrong user" {
            val booking = addLongBooking()
            should { bookingService.getBookingById(testBookingUser1, 1L).equals(booking) }
            shouldThrow<java.lang.IllegalStateException> {
                bookingService.deleteBooking(testBookingUser2, booking.id)
            }
        }
    }
})