// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.analysis

import de.lgblaumeiser.ptm.service.ActivityService
import de.lgblaumeiser.ptm.service.BookingService
import java.time.LocalDate

abstract class AbstractComputer<T>(val activityService: ActivityService, val bookingService: BookingService) :
    Analysis<T> {
    protected fun oneDayOnly(firstDay: String, firstDayAfter: String) =
        LocalDate.parse(firstDay).plusDays(1L).isEqual(LocalDate.parse(firstDayAfter))

}