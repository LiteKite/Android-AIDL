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

package com.litekite.server.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import com.litekite.connector.IBankService
import com.litekite.connector.IBankServiceCallback
import com.litekite.server.R

/**
 * @author Vignesh S
 * @version 1.0, 05/01/2020
 * @since 1.0
 */
class BankService : Service() {

	companion object {
		val TAG: String = BankService::class.java.simpleName
	}

	/**
	 * This is a list of callbacks that have been registered with the service.
	 */
	val bankServiceCallbacks: RemoteCallbackList<IBankServiceCallback> = RemoteCallbackList()

	private val binder = object : IBankService.Stub() {

		override fun registerCallback(cb: IBankServiceCallback?) {
			if (cb != null) {
				bankServiceCallbacks.register(cb)
			}
		}

		override fun unregisterCallback(cb: IBankServiceCallback?) {
			if (cb != null) {
				bankServiceCallbacks.unregister(cb)
			}
		}

		override fun signupRequest(username: String?, password: String?) {
			TODO("Not yet implemented")
		}

		override fun loginRequest(username: String?, password: String?) {
			TODO("Not yet implemented")
		}

	}

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
			return binder
		} else if (getString(R.string.action_bank_service_local_bind) == intent.action) {
			return LocalBinder()
		}
		return null
	}

	override fun onDestroy() {
		super.onDestroy()
		bankServiceCallbacks.kill()
	}

	override fun onLowMemory() {
		super.onLowMemory()
		Log.d(TAG, "onLowMemory")
	}

	@Suppress("UNUSED")
	inner class LocalBinder : Binder() {
		val service = this@BankService
	}

}