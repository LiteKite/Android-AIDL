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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.litekite.connector.R
import com.litekite.connector.entity.AuthResponse
import com.litekite.connector.entity.FailureResponse
import com.litekite.connector.entity.LoginRequest
import com.litekite.connector.entity.SignupRequest

/**
 * @author Vignesh S
 * @version 1.0, 12/01/2021
 * @since 1.0
 */
class BankServiceConnector private constructor(private val context: Context) {

	companion object {
		val TAG: String = BankServiceConnector::class.java.simpleName
	}

	private var serviceConnected = false
	private var bankService: IBankService? = null
	var callback: Callback? = null

	private val serviceConnection = object : ServiceConnection {

		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Log.d(TAG, "onServiceConnected")
			serviceConnected = true
			bankService = IBankService.Stub.asInterface(service)
			try {
				bankService?.registerCallback(bankServiceCallback)
			} catch (e: RemoteException) {
				e.printStackTrace()
			}
		}

		override fun onServiceDisconnected(name: ComponentName?) {
			Log.d(TAG, "onServiceDisconnected")
			serviceConnected = false
			bankService = null
		}

	}

	private val bankServiceCallback = object : IBankServiceCallback.Stub() {

		override fun onSignupResponse(authResponse: AuthResponse) {
			callback?.onSignupResponse(authResponse)
		}

		override fun onLoginResponse(authResponse: AuthResponse) {
			callback?.onLoginResponse(authResponse)
		}

		override fun onFailureResponse(failureResponse: FailureResponse) {
			callback?.onFailureResponse(failureResponse)
		}

	}

	fun connectService() {
		if (serviceConnected) {
			Log.d(TAG, "connectService: service was already connected. Ignoring...")
			return
		}
		val intent = Intent()
		intent.component = ComponentName.unflattenFromString(
			context.getString(R.string.component_bank_service)
		)
		intent.action = context.getString(R.string.action_bank_service)
		context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
	}

	fun signupRequest(signupRequest: SignupRequest) {
		if (!serviceConnected) {
			Log.d(TAG, "signupRequest: service was not connected. Ignoring...")
			return
		}
		try {
			bankService?.signupRequest(signupRequest)
		} catch (e: RemoteException) {
			e.printStackTrace()
		}
	}

	fun loginRequest(loginRequest: LoginRequest) {
		if (!serviceConnected) {
			Log.d(TAG, "loginRequest: service was not connected. Ignoring...")
			return
		}
		try {
			bankService?.loginRequest(loginRequest)
		} catch (e: RemoteException) {
			e.printStackTrace()
		}
	}

	fun disconnectService() {
		if (!serviceConnected) {
			Log.d(TAG, "disconnectService: service was not connected. Ignoring...")
			return
		}
		try {
			bankService?.unregisterCallback(bankServiceCallback)
		} catch (e: RemoteException) {
			e.printStackTrace()
		}
		context.unbindService(serviceConnection)
		serviceConnected = false
	}

	class Builder(context: Context) {

		private val connectorClient = BankServiceConnector(context)

		fun setCallback(cb: Callback): Builder {
			connectorClient.callback = cb
			return this@Builder
		}

		fun build(): BankServiceConnector {
			return connectorClient
		}

	}

	interface Callback {

		fun onSignupResponse(authResponse: AuthResponse) {}

		fun onLoginResponse(authResponse: AuthResponse) {}

		fun onFailureResponse(failureResponse: FailureResponse)

	}

}