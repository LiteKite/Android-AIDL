/*
 * Copyright 2021 LiteKite Startup. All rights reserved.
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
package com.litekite.server.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.litekite.server.room.dao.BankDao
import com.litekite.server.room.entity.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Database Class, Creates Database, Database Instance and destroys Database instance.
 *
 * @author Vignesh S
 *
 * @see [Room Library Guide](https://developer.android.com/topic/libraries/architecture/room.html)
 *
 * @see [Reference Guide](https://developer.android.com/reference/android/arch/persistence/room/package-summary.html)
 *
 * @version 1.0, 04/03/2018
 * @since 1.0
 */
@Database(entities = [UserAccount::class], version = 1)
abstract class BankDatabase : RoomDatabase() {

    companion object {

        const val ONE_ROW_UPDATED: Int = 1

        private const val DATABASE_NAME = "bank_database"

        @Volatile
        private var BANK_DATABASE_INSTANCE: BankDatabase? = null

        /**
         * Creates Room Database Instance if was not already initiated.
         *
         * @param context Activity or Application Context.
         *
         * @return [BANK_DATABASE_INSTANCE]
         */
        @Synchronized
        fun getBankDatabase(context: Context): BankDatabase {
            if (BANK_DATABASE_INSTANCE == null) {
                BANK_DATABASE_INSTANCE =
                    Room.databaseBuilder(context, BankDatabase::class.java, DATABASE_NAME).build()
            }
            return BANK_DATABASE_INSTANCE as BankDatabase
        }
    }

    suspend fun isUserExists(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            bankDao.isUserAccountExists(username)
        }
    }

    suspend fun saveUserAccount(userAccount: UserAccount): Long {
        return withContext(Dispatchers.IO) {
            bankDao.saveUserAccount(userAccount)
        }
    }

    suspend fun updateUserAccount(userAccount: UserAccount): Int {
        return withContext(Dispatchers.IO) {
            bankDao.updateUserAccount(userAccount)
        }
    }

    suspend fun getUserAccount(username: String, password: String): UserAccount? {
        return withContext(Dispatchers.IO) {
            bankDao.getUserAccount(username, password)
        }
    }

    suspend fun getUserAccount(userId: Long): UserAccount? {
        return withContext(Dispatchers.IO) {
            bankDao.getUserAccount(userId)
        }
    }

    /**
     * Destroys [BANK_DATABASE_INSTANCE]
     */
    fun destroyAppDatabase() {
        BANK_DATABASE_INSTANCE = null
    }

    /**
     * Gives BankDao Database Operations.
     *
     * @return BankDao abstract implementation.
     */
    abstract val bankDao: BankDao
}
