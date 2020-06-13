// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service

import java.lang.RuntimeException

class NotFoundException(message: String): RuntimeException(message)

class UserAccessException(message: String): RuntimeException(message)

class StorageException(message: String): RuntimeException(message)