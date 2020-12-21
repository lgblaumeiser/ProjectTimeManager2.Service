// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.store

interface Store<T> {
    fun retrieveAll(user: String): List<T>

    // Throws IllegalStateException, if element is not in storage or not accesible for user
    fun retrieveById(user: String, id: Long): T

    fun retrieveByProperty(user: String, name: String, values: Collection<Any>): List<T>

    fun create(user: String, data: T): T

    fun update(user: String, data: T)

    fun delete(user: String, id: Long)
}