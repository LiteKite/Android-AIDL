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

package com.litekite.server.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.litekite.connector.controller.IBankService
import com.litekite.connector.controller.IBankServiceCallback
import com.litekite.connector.entity.*
import com.litekite.server.R
import com.litekite.server.room.db.BankDatabase
import com.litekite.server.room.entity.UserAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * @author Vignesh S
 * @version 1.0, 05/01/2021
 * @since 1.0
 */
class BankService : Service() {

	companion object {
		val TAG: String = BankService::class.java.simpleName
	}

	/**
	 * A coroutines scope that holds all the coroutine works.
	 */
	private val coroutineScope = CoroutineScope(Dispatchers.IO)

	/**
	 * This is a list of callbacks that have been registered with the service.
	 */
	private val bankServiceCallbacks: RemoteCallbackList<IBankServiceCallback> =
		RemoteCallbackList()

	override fun onCreate() {
		super.onCreate()
		Log.d(TAG, "onCreate")
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		return START_STICKY
	}

	override fun onBind(intent: Intent?): IBinder? {
		if (intent == null) {
			return null
		}
		if (getString(R.string.action_bank_service) == intent.action) {
			return BankBinder()
		} else if (getString(R.string.action_bank_service_local_bind) == intent.action) {
			return LocalBinder()
		}
		return null
	}

	override fun onDestroy() {
		super.onDestroy()
		bankServiceCallbacks.kill()
		BankDatabase.destroyAppDatabase()
	}

	override fun onLowMemory() {
		super.onLowMemory()
		Log.d(TAG, "onLowMemory")
	}

	inner class BankBinder : IBankService.Stub() {

		override fun registerCallback(cb: IBankServiceCallback?) {
			if (cb != null) {
				bankServiceCallbacks.register(cb, this@BankBinder)
			}
		}

		override fun unregisterCallback(cb: IBankServiceCallback?) {
			if (cb != null) {
				bankServiceCallbacks.unregister(cb)
			}
		}

		override fun signupRequest(signupRequest: SignupRequest?) {
			if (signupRequest == null) {
				return
			}
			coroutineScope.launch {
				val isUserExists = BankDatabase.isUserExists(
					this@BankService,
					signupRequest.username
				)
				if (isUserExists) {
					postFailureResponse(RequestCode.SIGNUP, ResponseCode.ERROR_SIGN_UP_USER_EXISTS)
				} else {
					val userAccount = UserAccount(
						username = signupRequest.username,
						password = signupRequest.password
					)
					val userId = BankDatabase.saveUserAccount(this@BankService, userAccount)
					remoteBroadcast { index ->
						bankServiceCallbacks.getBroadcastItem(index).onSignupResponse(
							AuthResponse(ResponseCode.OK, userId, userAccount.username)
						)
					}
				}
			}
		}

		override fun loginRequest(loginRequest: LoginRequest?) {
			if (loginRequest == null) {
				return
			}
			coroutineScope.launch {
				val isUserExists = BankDatabase.isUserExists(
					this@BankService,
					loginRequest.username
				)
				if (!isUserExists) {
					postFailureResponse(
						RequestCode.LOGIN,
						ResponseCode.ERROR_LOG_IN_USER_NOT_EXISTS
					)
				} else {
					val userAccount = BankDatabase.getUserAccount(
						this@BankService,
						loginRequest.username,
						loginRequest.password
					)
					if (userAccount == null) {
						postFailureResponse(
							RequestCode.LOGIN,
							ResponseCode.ERROR_LOG_IN_INCORRECT_USER_NAME_OR_PASSWORD
						)
					} else {
						remoteBroadcast { index ->
							bankServiceCallbacks.getBroadcastItem(index).onLoginResponse(
								AuthResponse(
									ResponseCode.OK,
									userAccount.userId,
									userAccount.username
								)
							)
						}
					}
				}
			}
		}

		override fun userDetailsRequest(userId: Long) {
			coroutineScope.launch {
				val userAccount = BankDatabase.getUserAccount(this@BankService, userId)
				if (userAccount != null) {
					remoteBroadcast { index ->
						bankServiceCallbacks.getBroadcastItem(index).onUserDetailsResponse(
							UserDetails(userAccount.username, userAccount.balance)
						)
					}
				} else {
					postFailureResponse(
						RequestCode.USER_DETAILS_REQ,
						ResponseCode.ERROR_USER_NOT_FOUND
					)
				}
			}
		}

		override fun depositRequest(userId: Long, amount: Double) {
			coroutineScope.launch {
				val userAccount = BankDatabase.getUserAccount(this@BankService, userId)
				if (userAccount != null) {
					userAccount.balance += amount
					val noOfRowsUpdated = BankDatabase.updateUserAccount(
						this@BankService,
						userAccount
					)
					if (noOfRowsUpdated == BankDatabase.ONE_ROW_UPDATED) {
						postCurrentBalanceChanged(userAccount.balance)
					}
				} else {
					postFailureResponse(RequestCode.DEPOSIT, ResponseCode.ERROR_USER_NOT_FOUND)
				}
			}
		}

		override fun withdrawRequest(userId: Long, amount: Double) {
			coroutineScope.launch {
				val userAccount = BankDatabase.getUserAccount(this@BankService, userId)
				if (userAccount != null) {
					if (userAccount.balance == 0.0) {
						postFailureResponse(
							RequestCode.WITHDRAWAL,
							ResponseCode.ERROR_WITHDRAWAL_CURRENT_BALANCE_IS_ZERO
						)
						return@launch
					}
					userAccount.balance -= amount
					if (userAccount.balance < 0) {
						postFailureResponse(
							RequestCode.WITHDRAWAL,
							ResponseCode.ERROR_WITHDRAWAL_AMOUNT_EXCEEDS_CURRENT_BALANCE
						)
						return@launch
					}
					val noOfRowsUpdated = BankDatabase.updateUserAccount(
						this@BankService,
						userAccount
					)
					if (noOfRowsUpdated == BankDatabase.ONE_ROW_UPDATED) {
						postCurrentBalanceChanged(userAccount.balance)
					}
				} else {
					postFailureResponse(RequestCode.WITHDRAWAL, ResponseCode.ERROR_USER_NOT_FOUND)
				}
			}
		}

		private fun postFailureResponse(
			@RequestCode requestCode: Int,
			@ResponseCode responseCode: Int
		) {
			remoteBroadcast { index ->
				bankServiceCallbacks.getBroadcastItem(index).onFailureResponse(
					FailureResponse(requestCode, responseCode)
				)
			}
		}

		private fun postCurrentBalanceChanged(currentBalance: Double) {
			remoteBroadcast { index ->
				bankServiceCallbacks.getBroadcastItem(index).onCurrentBalanceChanged(
					currentBalance
				)
			}
		}

		private fun remoteBroadcast(block: (Int) -> Unit) {
			val count = bankServiceCallbacks.beginBroadcast()
			for (index in 0 until count) {
				if (bankServiceCallbacks.getBroadcastCookie(index) == this@BankBinder) {
					block.invoke(index)
					break
				}
			}
			bankServiceCallbacks.finishBroadcast()
		}

	}

	@Suppress("UNUSED")
	inner class LocalBinder : Binder() {
		val service = this@BankService
	}

}