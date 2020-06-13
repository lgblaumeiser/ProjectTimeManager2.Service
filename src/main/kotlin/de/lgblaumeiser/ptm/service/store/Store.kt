// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.store

interface Store<T> {
    fun retrieveAll(): List<T>

    // Throws NotFoundException, if element is not in storage
    fun retrieveById(id: Long): T

    fun retrieveByProperty(name: String, values: Collection<Any>): List<T>

    fun create(data: T): Long

    fun update(data: T)

    fun delete(id: Long)
}