// SPDX-FileCopyrightText: 2021 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

import de.lgblaumeiser.ptm.service.activityService
import de.lgblaumeiser.ptm.service.bookingService
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class ActivityComputerTest : WordSpec({

    beforeTest {
        createTestdatabase()
    }

    val testee = ActivityComputer(activityService, bookingService)

    "activity computer" should {
        "compute properly for a one month time period" {
            val analysisResults = testee.analyze(testAnalysisUsername, "2017-03-01", "2017-04-01")
            analysisResults.size.shouldBe(4)
            analysisResults.map { it.percentage }.reduce(Double::plus).shouldBe(200.0)
            analysisResults.map { it.minutes }.reduce(Duration::plus).toMinutes().shouldBe(4060L)
            analysisResults.filter { it.comment.isNotBlank() }.joinToString { it.comment }.shouldBe("")
        }

        "compute properly for a day period" {
            val analysisResults = testee.analyze(testAnalysisUsername, "2017-03-15", "2017-03-16")
            analysisResults.size.shouldBe(3)
            analysisResults.map { it.percentage }.reduce(Double::plus).shouldBe(200.0)
            analysisResults.map { it.minutes }.reduce(Duration::plus).toMinutes().shouldBe(1260L)
            analysisResults.filter { it.comment.isNotBlank() }.joinToString { it.comment }
                .shouldBe("$testAnalysisComment2, $testAnalysisComment3")
        }
    }
})