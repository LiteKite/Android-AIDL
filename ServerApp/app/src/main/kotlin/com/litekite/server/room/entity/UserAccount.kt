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

package com.litekite.server.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @author Vignesh S
 * @version 1.0, 20/01/2021
 * @since 1.0
 */
@Entity(tableName = "user_account", indices = [Index("user_id")])
data class UserAccount(

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "user_id")
	val userId: Int = 0,

	@ColumnInfo(name = "username")
	val username: String,

	@ColumnInfo(name = "password")
	val password: String,

	@ColumnInfo(name = "balance")
	val balance: Int = 0

)