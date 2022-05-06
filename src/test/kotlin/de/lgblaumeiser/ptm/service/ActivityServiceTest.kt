// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class ActivityServiceTest : WordSpec({

    beforeTest {
        initializeActivityService()
    }

    "getActivities" should {
        "return an empty collection when nothing is stored" {
            should { activityService.getActivities(testActivityUser1).shouldBeEmpty() }
        }
    }

    "getActivityById" should {
        "return an exception if asked for a id that do not exist" {
            shouldThrow<IllegalArgumentException> { activityService.getActivityById(testActivityUser1, 1L) }
        }
    }

    "addActivity" should {
        "added activity can be retrieved again" {
            val activity = addStandardActivity1()
            should {
                activity == testActivity1
                activityService.getActivities(testActivityUser1).shouldContainExactly(testActivity1.copy(id = 1L))
            }
        }

        "added activity not retrieved for different user" {
            addStandardActivity1()
            should { activityService.getActivities(testActivityUser2).shouldBeEmpty() }
        }

        "two added activity with different users should be separated" {
            addStandardActivity1()
            addStandardActivity2()
            should {
                activityService.getActivities(testActivityUser1).shouldContainExactly(testActivity1.copy(id = 1L))
                activityService.getActivities(testActivityUser2).shouldContainExactly(testActivity2.copy(id = 2L))
            }
        }

        "added activity can be retrieved by id" {
            addStandardActivity1()
            should { activityService.getActivityById(testActivityUser1, 1L) == testActivity1.copy(id = 1L) }
        }

        "added activity creates an exception if called with wrong user" {
            addStandardActivity1()
            shouldThrow<IllegalAccessException> { activityService.getActivityById(testActivityUser2, 1L) }
        }

        "two added activities for same user can be retrieved in sorted order" {
            val activity1 =
                activityService.addActivity(
                    testActivityUser1,
                    testProjectname1,
                    testProjectid1,
                    testActivityname1,
                    testActivityid1
                )
            val activity2 =
                activityService.addActivity(
                    testActivityUser1,
                    testProjectname2,
                    testProjectid2,
                    testActivityname2,
                    testActivityid2
                )
            val activity3 =
                activityService.addActivity(
                    testActivityUser1,
                    testProjectname1,
                    testProjectid1,
                    testActivityname2,
                    testActivityid2
                )
            should {
                val activities = activityService.getActivities(testActivityUser1)
                activities.size.shouldBe(3)
                activities[0].shouldBe(activity1)
                activities[1].shouldBe(activity3)
                activities[2].shouldBe(activity2)
            }
        }
    }

    "change activity" should {
        "added activity is changeable" {
            val activity = addStandardActivity1()
            activityService.changeActivity(
                user = testActivityUser1,
                id = activity.id,
                activityid = testActivityid2,
                activityname = testActivityname2
            )
            val retrieved = activityService.getActivityById(testActivityUser1, activity.id)
            should {
                activity != retrieved
                activity.id == retrieved.id
                activity.projectid == retrieved.projectid
                activity.projectname == retrieved.projectname
                activity.activityid != retrieved.activityid
                activity.activityname != retrieved.activityname
                activity.hidden == retrieved.hidden
            }
        }

        "added activity is not changeable by different user" {
            val activity = addStandardActivity1()
            shouldThrow<IllegalAccessException> {
                activityService.changeActivity(
                    user = testActivityUser2,
                    id = activity.id,
                    activityid = testActivityid2,
                    activityname = testActivityname2
                )
            }
        }

        "activities can be hidden" {
            val activity1 = addStandardActivity1()
            val activity2 =
                activityService.addActivity(
                    testActivityUser1,
                    testProjectname2,
                    testProjectid2,
                    testActivityname2,
                    testActivityid2
                )
            activityService.changeActivity(
                user = testActivityUser1,
                id = activity1.id,
                hidden = true
            )
            val retrieved1 = activityService.getActivityById(testActivityUser1, activity1.id)
            val retrieved2 = activityService.getActivityById(testActivityUser1, activity2.id)
            should {
                !activity1.hidden
                !activity2.hidden
                retrieved1.hidden
                !retrieved2.hidden
                activityService.getActivities(testActivityUser1).shouldContainExactly(retrieved2)
                activityService.getActivities(testActivityUser1, true).shouldContain(retrieved1)
            }
        }
    }

    "delete activity" should {
        "an added activity can be deleted" {
            val activity = addStandardActivity1()
            activityService.deleteActivity(testActivityUser1, activity.id)
            should { activityService.getActivities(testActivityUser1).shouldBeEmpty() }
        }

        "an added activity cannot be deleted from other user" {
            val activity = addStandardActivity1()
            shouldThrow<IllegalAccessException> { activityService.deleteActivity(testActivityUser2, activity.id) }
        }
    }
})