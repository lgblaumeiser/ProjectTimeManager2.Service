// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

import de.lgblaumeiser.ptm.service.bookingService
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class HourComputerTest : WordSpec({

    beforeTest {
        createTestdatabase()
    }

    val testee = HourComputer(bookingService)

    "Hour computer" should {
        "compute properly for the month" {
            val analysisResult = testee.analyze(testAnalysisUsername, "2017-03-01", "2017-04-01")
            analysisResult.size.shouldBe(6)
            analysisResult[5].overtime!!.toMinutes().shouldBe(-370L)
            analysisResult[4].comment.shouldContain(INCOMPLETE_COMMENT)
            analysisResult[5].comment.shouldContain(BREAKTIME_COMMENT)
        }

        "compute properly for a week with overlaps" {
            bookingService.addBooking(
                user = testAnalysisUsername,
                bookingday = dateToString(testAnalysisDate2),
                starttime = timeToString(testAnalysisTime3),
                endtime = timeToString(testAnalysisTime5),
                activity = testAnalysisActivityObjectId3
            )
            bookingService.addBooking(
                user = testAnalysisUsername,
                bookingday = dateToString(testAnalysisDate3),
                starttime = timeToString(testAnalysisTime8),
                endtime = timeToString(testAnalysisTime9),
                activity = testAnalysisActivityObjectId2
            )
            val analysisResult = testee.analyze(testAnalysisUsername, "2017-03-06", "2017-03-13")
            analysisResult.size.shouldBe(2)
            analysisResult[0].comment.shouldContain(OVERLAPPING_COMMENT)
            analysisResult[1].comment.shouldContain(WORKTIME_COMMENT)
            analysisResult[1].overtime!!.toMinutes().shouldBe(163L)
        }
    }
})