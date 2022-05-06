// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

data class User(
    val id: Long = -1,
    val username: String,
    val password: String,
    val admin: Boolean = false
) {
    init {
        require(username.isNotBlank()) { "username field must not be empty for user" }
        require(password.isNotBlank()) { "password field must not be empty for user" }
    }
}