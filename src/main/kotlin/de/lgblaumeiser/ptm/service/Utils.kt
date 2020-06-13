// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import de.lgblaumeiser.ptm.service.model.Activity

fun sameUser(owner: String, requester: String) =
    owner.equals(requester, true)

fun differentUser(owner: String, requester: String) =
    !sameUser(owner, requester)
