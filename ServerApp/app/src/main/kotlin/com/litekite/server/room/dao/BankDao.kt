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
package com.litekite.server.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.litekite.server.room.entity.UserAccount

/**
 * @author Vignesh S
 * @version 1.0, 20/01/2021
 * @since 1.0
 */
@Dao
interface BankDao {

    @Query("select exists(select * from user_account where username = :username)")
    suspend fun isUserAccountExists(username: String): Boolean

    @Query("select * from user_account where username = :username AND password = :password")
    suspend fun getUserAccount(username: String, password: String): UserAccount?

    @Query("select * from user_account where user_id = :userId")
    suspend fun getUserAccount(userId: Long): UserAccount?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserAccount(userAccount: UserAccount): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserAccount(userAccount: UserAccount): Long
}
