// SPDX-FileCopyrightText: 2020, 2022 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.store

import de.lgblaumeiser.ptm.service.*
import de.lgblaumeiser.ptm.service.model.Activity

class ActivityTestStoreComplianceTest : StoreContractComplianceKit<Activity>() {

    val store = ActivityTestStore()

    override fun store() = store

    override fun testObject1() = testActivity1.copy(user = userid1)

    override fun testObject1Updated(id: Long) =
        testActivity1.copy(id = id, user = userid1, projectname = testProjectname2, projectid = testProjectid2)

    override fun testObject2() = testActivity2.copy(user = userid2)

    override fun compareDataToStored(tostore: Activity, stored: Activity) = stored == tostore.copy(id = stored.id)

    override fun id(obj: Activity) = obj.id

    override fun username(obj: Activity) = obj.user

    override fun clearStore() {
        store.clear()
    }
}