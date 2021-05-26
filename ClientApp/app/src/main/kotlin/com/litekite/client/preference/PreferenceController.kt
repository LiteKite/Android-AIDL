/*
 * Copyright 2020-2021 LiteKite Startup. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.litekite.client.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A Preference DataStore Controller that uses Credential Protected Storage and it can be only
 * accessed after credentials for lock-screen being entered and unlocked.
 *
 * These preferences are stored in /data/user[release-version] or in /data/user-de[debug-version]
 *
 * @author Vignesh S
 * @version 1.0, 09/04/2020
 * @since 1.0
 */
@Suppress("UNUSED")
@Singleton
class PreferenceController @Inject constructor(private val context: Context) {

    companion object {

        const val PREFERENCES_CLIENT_APP = "preferences_client_app"

        const val PREFERENCE_LOGIN_COMPLETE_STATE = "preference_login_complete_state"

        const val PREFERENCE_LOGGED_IN_USER_ID = "preference_logged_in_user_id"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        PREFERENCES_CLIENT_APP
    )

    private val scope = CoroutineScope(Dispatchers.IO)

    fun getBoolean(key: String): Flow<Boolean> = context.dataStore.data.map { pref ->
        pref[booleanPreferencesKey(key)] ?: false
    }.flowOn(
        Dispatchers.IO
    ).catch {
        it.printStackTrace()
    }

    fun getInt(key: String): Flow<Int> = context.dataStore.data.map { pref ->
        pref[intPreferencesKey(key)] ?: 0
    }.flowOn(
        Dispatchers.IO
    ).catch {
        it.printStackTrace()
    }

    fun getLong(key: String): Flow<Long> = context.dataStore.data.map { pref ->
        pref[longPreferencesKey(key)] ?: 0L
    }.flowOn(
        Dispatchers.IO
    ).catch {
        it.printStackTrace()
    }

    fun getFloat(key: String): Flow<Float> = context.dataStore.data.map { pref ->
        pref[floatPreferencesKey(key)] ?: 0F
    }.flowOn(
        Dispatchers.IO
    ).catch {
        it.printStackTrace()
    }

    fun getDouble(key: String): Flow<Double> = context.dataStore.data.map { pref ->
        java.lang.Double.longBitsToDouble(pref[longPreferencesKey(key)] ?: 0)
    }.flowOn(
        Dispatchers.IO
    ).catch {
        it.printStackTrace()
    }

    fun getString(key: String): Flow<String> = context.dataStore.data.map { pref ->
        pref[stringPreferencesKey(key)] ?: ""
    }.flowOn(
        Dispatchers.IO
    ).catch {
        it.printStackTrace()
    }

    fun store(key: String, value: Boolean) = set(booleanPreferencesKey(key), value)

    fun store(key: String, value: Int) = set(intPreferencesKey(key), value)

    fun store(key: String, value: Long) = set(longPreferencesKey(key), value)

    fun store(key: String, value: Float) = set(floatPreferencesKey(key), value)

    fun store(key: String, value: Double) = set(
        longPreferencesKey(key),
        java.lang.Double.doubleToRawLongBits((value))
    )

    fun store(key: String, value: String) = set(stringPreferencesKey(key), value)

    private fun <T> set(prefKey: Preferences.Key<T>, value: T) = scope.launch {
        context.dataStore.edit { pref -> pref[prefKey] = value }
    }
}
