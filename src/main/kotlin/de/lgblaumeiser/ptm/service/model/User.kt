// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.model

data class User(
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
        require(username.isNotBlank()) { "username field must not be empty for user" }
        require(password.isNotBlank()) { "password field must not be empty for user" }
        require(question.isNotBlank()) { "question field must not be empty for user" }
        require(answer.isNotBlank()) { "answer field must not be empty for user" }
        require(pattern.matches(email)) { "email field does not correspond to an email address, was $email" }
    }
}