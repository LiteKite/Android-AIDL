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

package com.litekite.connector.entity

/**
 * @author Vignesh S
 * @version 1.0, 20/01/2021
 * @since 1.0
 */
@Suppress("UNUSED")
@Retention(AnnotationRetention.SOURCE)
annotation class ResponseCode {

	companion object {
		const val ERROR_SIGN_UP_USER_EXISTS = -6
		const val ERROR_LOG_IN_USER_NOT_EXISTS = -5
		const val ERROR_LOG_IN_INCORRECT_USER_NAME_OR_PASSWORD = -4
		const val ERROR_USER_NOT_FOUND = -3
		const val ERROR_WITHDRAWAL_CURRENT_BALANCE_IS_ZERO = -2
		const val ERROR_WITHDRAWAL_AMOUNT_EXCEEDS_CURRENT_BALANCE = -1
		const val OK = 0
	}

}