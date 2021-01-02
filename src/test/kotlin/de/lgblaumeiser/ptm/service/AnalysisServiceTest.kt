// SPDX-FileCopyrightText: 2021 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.analysis.BREAKTIME_COMMENT
import de.lgblaumeiser.ptm.service.analysis.INCOMPLETE_COMMENT
import de.lgblaumeiser.ptm.service.analysis.createTestdatabase
import de.lgblaumeiser.ptm.service.analysis.testAnalysisUsername
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.Duration

class AnalysisServiceTest : WordSpec({

    beforeTest {
        createTestdatabase()
    }

    val testee = AnalysisService(activityService, bookingService)

    "hour analysis" should {
        val analysisResult = testee.runHourAnalysis(testAnalysisUsername, "2017-03-01", "2017-04-01")
        analysisResult.size.shouldBe(6)
        analysisResult[5].overtime.shouldBe("-06:10")
        analysisResult[4].comment.shouldContain(INCOMPLETE_COMMENT)
        analysisResult[5].comment.shouldContain(BREAKTIME_COMMENT)
    }

    "project analysis" should {
        val analysisResults = testee.runProjectAnalysis(testAnalysisUsername, "2017-03-01", "2017-04-01")
        analysisResults.size.shouldBe(3)
        analysisResults.map { it.percentage.substring(0, it.percentage.length - 1).toDouble() }.reduce(Double::plus)
            .shouldBe(200.0)
    }

    "activity analysis" should {
        val analysisResults = testee.runActivityAnalysis(testAnalysisUsername, "2017-03-01", "2017-04-01")
        analysisResults.size.shouldBe(4)
        analysisResults.map { it.percentage.substring(0, it.percentage.length - 1).toDouble() }.reduce(Double::plus)
            .shouldBe(200.0)
    }
})