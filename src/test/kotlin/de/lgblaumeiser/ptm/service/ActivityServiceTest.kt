// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

val testUser1 = "userid1"
val testUser2 = "userid2"

val testProjectname1 = "project1"
val testProjectname2 = "project2"

val testProjectid1 = "0815"
val testProjectid2 = "4711"

val testActivityname1 = "activity1"
val testActivityname2 = "activity2"

val testActivityid1 = "1"
val testActivityid2 = "2"

class ActivityServiceTest : WordSpec ({

    fun initializeService() = ActivityService(ActivityTestStore())

    "getActivities" should {
        "return an empty collection when nothing is stored" {
            val service = initializeService()
            should { service.getActivities("testUser").shouldBeEmpty() }
        }
    }

    "getActivityById" should {
        "return an exception if asked for a id that do not exist" {
            val service = initializeService()
            shouldThrow<IllegalStateException> { service.getActivityById("testUser1", 1L) }
        }
    }

    "addActivity" should {
        "added activity can be retrieved again" {
            val service = initializeService()
            val activity = service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            should { service.getActivities(testUser1).shouldContainExactly(activity) }
        }

        "added activity not retrieved for different user" {
            val service = initializeService()
            service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            should { service.getActivities(testUser2).shouldBeEmpty() }
        }

        "two added activity with different users should be separated" {
            val service = initializeService()
            val activity1 = service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            val activity2 = service.addActivity(testUser2, testProjectname2, testProjectid2, testActivityname2, testActivityid2)
            should { service.getActivities(testUser1).shouldContainExactly(activity1) }
            should { service.getActivities(testUser2).shouldContainExactly(activity2) }
        }

        "added activity can be retrieved by id" {
            val service = initializeService()
            val activity = service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            should { service.getActivityById(testUser1, activity.id).equals(activity) }
        }

        "added activity creates an exception if called with wrong user" {
            val service = initializeService()
            val activity = service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            shouldThrow<IllegalStateException> { service.getActivityById(testUser2, activity.id) }
        }
    }
})