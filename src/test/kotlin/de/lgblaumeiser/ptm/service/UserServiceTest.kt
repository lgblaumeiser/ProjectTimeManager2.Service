package de.lgblaumeiser.ptm.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec

val testUsername1 = "user1"
val testUsername2 = "user2"

class UserServiceTest : WordSpec({

    fun initializeService() = UserService(UserTestStore(), ActivityService(ActivityTestStore()), BookingService(BookingTestStore()))

    "get user by name results in exception without users" should {
        val service = initializeService()
        shouldThrow<IllegalStateException> { service.getUser(testUsername1) }
    }
})
