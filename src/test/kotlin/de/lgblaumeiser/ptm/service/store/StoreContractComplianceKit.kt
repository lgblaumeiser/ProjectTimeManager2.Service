// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT
package de.lgblaumeiser.ptm.service.store

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

// Abstract test to ensure that a store implementation fulfils the contract

abstract class StoreContractComplianceKit<T> : WordSpec() {
    // return the store to test
    abstract fun store(): Store<T>

    // return test objects needed for the tests
    abstract fun testObject1(): T // userid1
    abstract fun testObject1Updated(id: Long): T
    abstract fun testObject2(): T // userid2

    // compares a new data object to the stored one. Not a simple equals since id is set by create method
    abstract fun compareDataToStored(tostore: T, stored: T): Boolean

    // return the id of the object
    abstract fun id(obj: T): Long

    // return the username stored in the object
    abstract fun username(obj: T): String

    abstract fun clearStore()

    val userid1 = "TestUserId"
    val userid2 = "TestUserIdToCheck"

    init {
        beforeTest {
            clearStore()
        }

        "create object" should {
            "create object allows to retrieve the object" {
                val tostore = testObject1()
                should { username(tostore) == userid1 }
                val created = store().create(userid1, tostore)
                should {
                    compareDataToStored(created, tostore)
                    store().retrieveAll(userid1).shouldContainExactly(created)
                    store().retrieveById(userid1, id(created))
                }
            }

            "create object created user specific object that cannot be retrieved for different user" {
                val tostore = testObject1()
                should { username(tostore) == userid1 }
                val created = store().create(userid1, tostore)
                should {
                    store().retrieveAll(userid2).shouldBeEmpty()
                }
                shouldThrow<IllegalStateException> { store().retrieveById(userid2, id(created)) }
            }

            "create object with two different users return the objects associated to the right user" {
                val tostore1 = testObject1()
                should { username(tostore1) == userid1 }
                val tostore2 = testObject2()
                should { username(tostore2) == userid2 }
                val created1 = store().create(userid1, tostore1)
                val created2 = store().create(userid2, tostore2)
                should {
                    compareDataToStored(created1, tostore1)
                    store().retrieveAll(userid1).shouldContainExactly(created1)
                    store().retrieveById(userid1, id(created1))
                    compareDataToStored(created2, tostore2)
                    store().retrieveAll(userid2).shouldContainExactly(created2)
                    store().retrieveById(userid2, id(created2))
                }
            }
        }

        "delete object" should {
            "Delete object should remove the referenced object" {
                val tostore = testObject1()
                should { username(tostore) == userid1 }
                val created = store().create(userid1, tostore)
                should { store().retrieveAll(userid1).shouldContainExactly(created) }
                store().delete(userid1, id(created))
                should { store().retrieveAll(userid1).shouldBeEmpty() }
            }

            "Delete object should not remove anything, if wrong user is given" {
                val tostore = testObject1()
                should { username(tostore) == userid1 }
                val created = store().create(userid1, tostore)
                should { store().retrieveAll(userid1).shouldContainExactly(created) }
                shouldThrow<IllegalStateException> { store().delete(userid2, id(created)) }
                should { store().retrieveAll(userid1).shouldContainExactly(created) }
            }
        }

        "update object" should {
            "Update object should work with apprpriate data" {
                val tostore = testObject1()
                should { username(tostore) == userid1 }
                val created = store().create(userid1, tostore)
                val toupdate = testObject1Updated(id(created))
                should { username(toupdate) == userid1 }
                should { id(toupdate) == id(created) }
                store().update(userid1, toupdate)
                should { store().retrieveById(userid1, id(created)).shouldBe(toupdate) }
            }

            "Update object should not change anything with wrong user" {
                val tostore = testObject1()
                should { username(tostore) == userid1 }
                val created = store().create(userid1, tostore)
                val toupdate = testObject1Updated(id(created))
                should { username(toupdate) == userid1 }
                should { id(toupdate) == id(created) }
                shouldThrow<IllegalArgumentException> { store().update(userid2, toupdate) }
                should { store().retrieveById(userid1, id(created)).shouldBe(created) }
            }
        }
    }
}