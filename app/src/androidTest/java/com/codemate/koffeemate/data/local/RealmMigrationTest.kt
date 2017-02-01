/*
 * Copyright 2017 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.data.local

import android.content.Context
import android.support.test.InstrumentationRegistry
import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import io.realm.Realm
import io.realm.RealmConfiguration
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.IsNull.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class RealmMigrationTest {
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getContext()
    }

    /*************************************************************
     * Migration tests from schema version 0 to 1
     *************************************************************/
    @Test
    fun testMigrationFromVersionZeroToOne() {
        val config = RealmConfiguration.Builder()
                .name("migration-test.realm")
                .schemaVersion(1)
                .migration(Migration())
                .build()

        // The "sample-db-schema-v0.realm" contains three sample records,
        // in the old database schema, which used userIds instead of User
        // objects.
        copyRealmFromAssets(context, "sample-db-schema-v0.realm", config)
        val realm = Realm.getInstance(config)

        val all = realm.where(CoffeeBrewingEvent::class.java).findAll()
        assertThat(all.size, equalTo(3))

        val first = all[0]
        assertThat(first.id, equalTo("adf9c9b9-e521-462f-9d67-ff2a11d7b62c"))
        assertThat(first.time, equalTo(1485872637115L))
        assertThat(first.isSuccessful, equalTo(true))
        assertThat(first.user, nullValue())

        val second = all[1]
        assertThat(second.id, equalTo("0e742762-7181-4bc0-b7b5-d1ff68991dd6"))
        assertThat(second.time, equalTo(1485872637117L))
        assertThat(second.isSuccessful, equalTo(true))
        assertThat(second.user!!.id, equalTo("abc-123"))
        assertThat(second.user!!.last_updated, not(equalTo(0L)))

        val third = all[2]
        assertThat(third.id, equalTo("480bb3b9-a01f-45cb-87cd-113465d4038a"))
        assertThat(third.time, equalTo(1485872637118L))
        assertThat(third.isSuccessful, equalTo(false))
        assertThat(third.user!!.id, equalTo("abc-123"))
        assertThat(second.user!!.last_updated, not(equalTo(0L)))

        // Make sure we don't generate different timestamps for the users in
        // this new schema.
        assertThat(second.user!!.last_updated, equalTo(third.user!!.last_updated))

        realm.close()
    }

    @Throws(IOException::class)
    fun copyRealmFromAssets(context: Context, realmPath: String, config: RealmConfiguration) {
        Realm.deleteRealm(config)

        context.assets.open(realmPath).use { inputStream ->
            val outFile = File(config.realmDirectory, config.realmFileName)

            outFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
