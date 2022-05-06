// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.store

import de.lgblaumeiser.ptm.service.*
import de.lgblaumeiser.ptm.service.model.Booking
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import java.time.LocalDate
import java.time.LocalTime

class BookingTestStoreComplianceTest : StoreContractComplianceKit<Booking>() {
    private val store = BookingTestStore()

    override fun store() = store

    override fun testObject1() = testBooking1.copy( user = userid1 )

    override fun testObject1Updated(id: Long) =
        testBooking1.copy(id = id, user = userid1, endtime = LocalTime.parse(testTime2), comment = testComment)

    override fun testObject2() = testBooking1.copy( user = userid2 )

    override fun compareDataToStored(tostore: Booking, stored: Booking) = stored == tostore.copy(id = stored.id)

    override fun id(obj: Booking) = obj.id

    override fun username(obj: Booking) = obj.user

    override fun clearStore() {
        store.clear()
    }

    init {
        "retrieve by id" should {
            "Retrieve by id works for the bookingsdays" {
                val tostore = testObject1()
                should { username(tostore) == userid1 }
                val created = store().create(tostore)
                should {
                     store().retrieveByBookingDays(userid1, listOf(LocalDate.parse(testDate1))).shouldContainExactly(created)
                }
            }
        }
    }
}