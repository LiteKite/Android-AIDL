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

package com.litekite.connector.controller

import android.content.Context
import com.litekite.connector.base.CallbackProvider
import com.litekite.connector.entity.AuthResponse
import com.litekite.connector.entity.FailureResponse
import com.litekite.connector.entity.LoginRequest
import com.litekite.connector.entity.SignupRequest

/**
 * @author Vignesh S
 * @version 1.0, 17/01/2021
 * @since 1.0
 */
@Suppress("UNUSED")
class BankServiceController(context: Context) : BankServiceConnector.Callback,
	CallbackProvider<BankServiceConnector.Callback> {

	private val serviceProvider = BankServiceConnector.Builder(context)
		.setCallback(this)
		.build()

	override val callbacks: ArrayList<BankServiceConnector.Callback> = ArrayList()

	override fun addCallback(cb: BankServiceConnector.Callback) {
		super.addCallback(cb)
		if (callbacks.size > 0) {
			connect()
		}
	}

	override fun removeCallback(cb: BankServiceConnector.Callback) {
		super.removeCallback(cb)
		if (callbacks.size == 0) {
			disconnect()
		}
	}

	private fun connect() {
		serviceProvider.connectService()
	}

	private fun disconnect() {
		serviceProvider.disconnectService()
	}

	fun signup(username: String, password: String) {
		val signupRequest = SignupRequest(username, password)
		serviceProvider.signupRequest(signupRequest)
	}

	fun login(username: String, password: String) {
		val loginRequest = LoginRequest(username, password)
		serviceProvider.loginRequest(loginRequest)
	}

	override fun onSignupResponse(authResponse: AuthResponse) {
		callbacks.forEach { it.onSignupResponse(authResponse) }
	}

	override fun onLoginResponse(authResponse: AuthResponse) {
		callbacks.forEach { it.onLoginResponse(authResponse) }
	}

	override fun onFailureResponse(failureResponse: FailureResponse) {
		callbacks.forEach { it.onFailureResponse(failureResponse) }
	}

}