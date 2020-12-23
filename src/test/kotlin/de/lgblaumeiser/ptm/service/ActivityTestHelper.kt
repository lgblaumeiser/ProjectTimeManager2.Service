package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity

const val testActivityUser1 = "userid1"
const val testActivityUser2 = "userid2"

const val testProjectname1 = "project1"
const val testProjectname2 = "project2"

const val testProjectid1 = "0815"
const val testProjectid2 = "4711"

const val testActivityname1 = "activity1"
const val testActivityname2 = "activity2"

const val testActivityid1 = "1"
const val testActivityid2 = "2"

val activityStore = ActivityTestStore()
val activityService = ActivityService(activityStore)

fun initializeActivityService() {
    activityStore.clear()
}

val testActivity1 =
    Activity(-1L, testActivityUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)
val testActivity2 =
    Activity(-1L, testActivityUser2, testProjectname2, testProjectid2, testActivityname2, testActivityid2)

fun addStandardActivity1() =
    activityService.addActivity(testActivityUser1, testProjectname1, testProjectid1, testActivityname1, testActivityid1)

fun addStandardActivity2() =
    activityService.addActivity(testActivityUser2, testProjectname2, testProjectid2, testActivityname2, testActivityid2)
