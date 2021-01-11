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

package com.litekite.server

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.RemoteCallbackList
import com.litekite.connector.IBankService
import com.litekite.connector.IBankServiceCallback

/**
 * @author Vignesh S
 * @version 1.0, 05/01/2020
 * @since 1.0
 */
class BankService : Service() {

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
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		return START_STICKY
	}

	override fun onBind(intent: Intent?): IBinder? {
		if (intent == null) {
			return null
		}
		if (IBankService::class.java.name == intent.action) {
			return binder
		} else if (BankService::class.java.name == intent.action) {
			return LocalBinder()
		}
		return null
	}

	override fun onTaskRemoved(rootIntent: Intent?) {
		super.onTaskRemoved(rootIntent)
	}

	inner class LocalBinder : Binder() {
		val service = this@BankService
	}

}