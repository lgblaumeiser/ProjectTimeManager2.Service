package de.lgblaumeiser.ptm.service.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should

val testActivityUser = "userid1"

val testProjectname = "project1"

val testProjectid = "0815"

val testActivityname = "activity1"

val testActivityid = "1"

class ActivityTest: WordSpec ({

    "Activity validation" should {
        "should be fine with proper fields" {
            val activity = Activity(
                user = testActivityUser,
                projectname = testProjectname,
                projectid = testProjectid,
                activityname = testActivityname,
                activityid = testActivityid)
            should {
                activity.id == -1L;
                activity.user.equals(testActivityUser)
                activity.projectname.equals(testProjectname)
                activity.projectid.equals(testProjectid)
                activity.activityname.equals(testActivityname)
                activity.activityid.equals(testActivityid)
                !activity.hidden
            }
        }

        "should be fine with proper fields and set id and hidden field" {
            val activity = Activity(
                user = testActivityUser,
                projectname = testProjectname,
                projectid = testProjectid,
                activityname = testActivityname,
                activityid = testActivityid,
                id = 1L,
                hidden = true
            )
            should {
                activity.id == 1L;
                activity.user.equals(testActivityUser)
                activity.projectname.equals(testProjectname)
                activity.projectid.equals(testProjectid)
                activity.activityname.equals(testActivityname)
                activity.activityid.equals(testActivityid)
                activity.hidden
            }
        }

        "should throw exception with blank user" {
            shouldThrow<IllegalArgumentException> {
                Activity(
                    user = "",
                    projectname = testProjectname,
                    projectid = testProjectid,
                    activityname = testActivityname,
                    activityid = testActivityid)
            }
        }

        "should throw exception with blank projectname" {
            shouldThrow<IllegalArgumentException> {
                Activity(
                    user = testActivityUser,
                    projectname = "",
                    projectid = testProjectid,
                    activityname = testActivityname,
                    activityid = testActivityid)
            }
        }

        "should throw exception with blank projectid" {
            shouldThrow<IllegalArgumentException> {
                Activity(
                    user = testActivityUser,
                    projectname = testProjectname,
                    projectid = "",
                    activityname = testActivityname,
                    activityid = testActivityid)
            }
        }

        "should throw exception with blank activityname" {
            shouldThrow<IllegalArgumentException> {
                Activity(
                    user = testActivityUser,
                    projectname = testProjectname,
                    projectid = testProjectid,
                    activityname = "",
                    activityid = testActivityid)
            }
        }

        "should throw exception with blank activityid" {
            shouldThrow<IllegalArgumentException> {
                Activity(
                    user = testActivityUser,
                    projectname = testProjectname,
                    projectid = testProjectid,
                    activityname = testActivityname,
                    activityid = "")
            }
        }
    }
})
