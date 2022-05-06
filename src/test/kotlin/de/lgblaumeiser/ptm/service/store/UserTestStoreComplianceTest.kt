// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.store

import de.lgblaumeiser.ptm.service.UserTestStore
import de.lgblaumeiser.ptm.service.model.User
import de.lgblaumeiser.ptm.service.testPassword2
import de.lgblaumeiser.ptm.service.testUser

class UserTestStoreComplianceTest : StoreContractComplianceKit<User>() {
    private val store = UserTestStore()

    override fun store() = store

    override fun testObject1() = testUser.copy(username = userid1)

    override fun testObject1Updated(id: Long) = testUser.copy(id = id, username = userid1, password = testPassword2)

    override fun testObject2() = testUser.copy(username = userid2)

    override fun compareDataToStored(tostore: User, stored: User) = stored == tostore.copy(id = stored.id)

    override fun id(obj: User) = obj.id

    override fun username(obj: User) = obj.username

    override fun clearStore() {
        store.clear()
    }
}