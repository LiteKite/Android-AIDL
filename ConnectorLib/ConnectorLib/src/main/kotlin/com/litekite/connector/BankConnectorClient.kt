/*
 * Copyright 2020 LiteKite Startup. All rights reserved.
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

package com.litekite.connector

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.litekite.connector.base.CallbackProvider

/**
 * @author Vignesh S
 * @version 1.0, 12/01/2020
 * @since 1.0
 */
@Suppress("UNUSED")
class BankConnectorClient(private val context: Context) :
	CallbackProvider<BankConnectorClient.Callback> {

	companion object {
		val TAG: String = BankConnectorClient::class.java.simpleName
	}

	private var isServiceConnected = false
	private var bankService: IBankService? = null

	private val serviceConnection = object : ServiceConnection {

		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			Log.d(TAG, "onServiceConnected")
			isServiceConnected = true
			try {
				bankService = IBankService.Stub.asInterface(service)
				bankService?.registerCallback(bankServiceCallback)
			} catch (e: RemoteException) {
				e.printStackTrace()
			}
		}

		override fun onServiceDisconnected(name: ComponentName?) {
			Log.d(TAG, "onServiceDisconnected")
			isServiceConnected = false
			bankService = null
		}

	}

	private val bankServiceCallback = object : IBankServiceCallback.Stub() {

		override fun onSignupResponse(responseState: Int, userId: Int, username: String?) {
			TODO("Not yet implemented")
		}

		override fun onLoginResponse(responseState: Int, userId: Int, username: String?) {
			TODO("Not yet implemented")
		}

	}

	override val callbacks: ArrayList<Callback> = ArrayList()

	init {
		connectService()
	}

	private fun connectService() {
		val intent = Intent()
		intent.component = ComponentName.unflattenFromString(
			context.getString(R.string.component_bank_service)
		)
		intent.action = context.getString(R.string.action_bank_service)
		context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
	}

	private fun disconnectService() {
		try {
			bankService?.unregisterCallback(bankServiceCallback)
		} catch (e: RemoteException) {
			e.printStackTrace()
		}
		context.unbindService(serviceConnection)
	}

	interface Callback {

		fun onSignupResponse()

		fun onLoginResponse()

	}

}