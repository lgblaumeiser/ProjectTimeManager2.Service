// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

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

class ActivityServiceTest : WordSpec({

    fun initializeService() = ActivityService(ActivityTestStore())

    "getActivities" should {
        "return an empty collection when nothing is stored" {
            val service = initializeService()
            should { service.getActivities(testUser1).shouldBeEmpty() }
        }
    }

    "getActivityById" should {
        "return an exception if asked for a id that do not exist" {
            val service = initializeService()
            shouldThrow<IllegalStateException> { service.getActivityById(testUser1, 1L) }
        }
    }

    "addActivity" should {
        "added activity can be retrieved again" {
            val service = initializeService()
            val activity =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            should { service.getActivities(testUser1).shouldContainExactly(activity) }
        }

        "added activity not retrieved for different user" {
            val service = initializeService()
            service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            should { service.getActivities(testUser2).shouldBeEmpty() }
        }

        "two added activity with different users should be separated" {
            val service = initializeService()
            val activity1 =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            val activity2 =
                service.addActivity(testUser2, testProjectname2, testProjectid2, testActivityname2, testActivityid2)
            should { service.getActivities(testUser1).shouldContainExactly(activity1) }
            should { service.getActivities(testUser2).shouldContainExactly(activity2) }
        }

        "added activity can be retrieved by id" {
            val service = initializeService()
            val activity =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            should { service.getActivityById(testUser1, activity.id).equals(activity) }
        }

        "added activity creates an exception if called with wrong user" {
            val service = initializeService()
            val activity =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            shouldThrow<IllegalStateException> { service.getActivityById(testUser2, activity.id) }
        }

        "two added activities for same user can be retrieved in sorted order" {
            val service = initializeService()
            val activity1 =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            val activity2 =
                service.addActivity(testUser1, testProjectname2, testProjectid2, testActivityname2, testActivityid2)
            val activity3 =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname2, testActivityid2)
            should {
                service.getActivityById(testUser1, activity1.id).shouldBe(activity1)
                service.getActivityById(testUser1, activity2.id).shouldBe(activity2)
                service.getActivityById(testUser1, activity3.id).shouldBe(activity3)
                val activities = service.getActivities(testUser1)
                activities.size.shouldBe(3)
                activities.get(0).shouldBe(activity1)
                activities.get(1).shouldBe(activity3)
                activities.get(2).shouldBe(activity2)
            }
        }
    }

    "change activity" should {
        "added activity is changeable" {
            val service = initializeService()
            val activity =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            service.changeActivity(
                user = testUser1,
                id = activity.id,
                activityid = testActivityid2,
                activityname = testActivityname2
            )
            val retrieved = service.getActivityById(testUser1, activity.id)
            should {
                !activity.equals(retrieved)
                activity.id == retrieved.id
                activity.projectid == retrieved.projectid
                activity.projectname == retrieved.projectname
                activity.activityid != retrieved.activityid
                activity.activityname != retrieved.activityname
                activity.hidden == retrieved.hidden
            }
        }

        "added activity is not changeable by different user" {
            val service = initializeService()
            val activity =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            shouldThrow<IllegalStateException> {
                service.changeActivity(
                    user = testUser2,
                    id = activity.id,
                    activityid = testActivityid2,
                    activityname = testActivityname2
                )
            }
        }

        "activities can be hidden" {
            val service = initializeService()
            val activity1 =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            val activity2 =
                service.addActivity(testUser1, testProjectname2, testProjectid2, testActivityname2, testActivityid2)
            service.changeActivity(
                user = testUser1,
                id = activity1.id,
                hidden = true
            )
            val retrieved1 = service.getActivityById(testUser1, activity1.id)
            val retrieved2 = service.getActivityById(testUser1, activity2.id)
            should {
                !activity1.hidden
                !activity2.hidden
                retrieved1.hidden
                !retrieved2.hidden
                !activity1.equals(retrieved1)
                activity2.equals(retrieved2)
                service.getActivities(testUser1).shouldContainExactly(retrieved2)
                service.getActivities(testUser1, true).shouldContain(retrieved1)
            }
        }
    }

    "delete activity" should {
        "an added activity can be deleted" {
            val service = initializeService()
            val activity =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            service.deleteActivity(testUser1, activity.id)
            should { service.getActivities(testUser1).shouldBeEmpty() }
        }

        "an added activity cannot be deleted from other user" {
            val service = initializeService()
            val activity =
                service.addActivity(testUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
            shouldThrow<java.lang.IllegalStateException> { service.deleteActivity(testUser2, activity.id) }
        }
    }
})