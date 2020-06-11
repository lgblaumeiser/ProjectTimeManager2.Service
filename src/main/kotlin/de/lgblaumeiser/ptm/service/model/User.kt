// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

data class User (
    val id: Long = -1,
    val username: String,
    val password: String,
    val email: String,
    val question: String,
    val answer: String,
    val admin: Boolean = false
) {
    private val pattern = "^(.+)@(.+)$".toRegex()

    init {
        require(pattern.containsMatchIn(email))
    }
}