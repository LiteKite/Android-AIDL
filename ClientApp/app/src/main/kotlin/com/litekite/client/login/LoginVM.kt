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

package com.litekite.client.login

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.litekite.client.R
import com.litekite.client.app.ClientApp
import com.litekite.client.signup.SignupActivity
import com.litekite.connector.controller.BankServiceConnector
import com.litekite.connector.controller.BankServiceController
import com.litekite.connector.entity.AuthResponse

/**
 * @author Vignesh S
 * @version 1.0, 19/01/2020
 * @since 1.0
 */
class LoginVM @ViewModelInject constructor(
	application: Application,
	private val bankServiceController: BankServiceController
) : AndroidViewModel(application), LifecycleObserver, BankServiceConnector.Callback {

	companion object {
		val TAG: String = LoginVM::class.java.simpleName
	}

	private val isLoginCompleted: MutableLiveData<Boolean> = MutableLiveData()

	val username: ObservableField<String> = ObservableField()
	val password: ObservableField<String> = ObservableField()

	fun getIsLoginCompleted(): LiveData<Boolean> {
		return isLoginCompleted
	}

	fun onClick(v: View) {
		when (v.id) {
			R.id.b_login -> {
				ClientApp.printLog(TAG, "username: ${username.get()} password: ${password.get()}")
				bankServiceController.login("${username.get()}", "${password.get()}")
			}
			R.id.tv_signup -> {
				SignupActivity.start(v.context)
			}
		}
	}

	override fun onLoginResponse(authResponse: AuthResponse) {
		super.onLoginResponse(authResponse)
		ClientApp.printLog(TAG, "onLoginResponse:")
		when (authResponse) {
			is AuthResponse.Success -> {

			}
			is AuthResponse.Failure -> {

			}
		}
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	fun onCreate() {
		bankServiceController.addCallback(this)
		bankServiceController.connect()
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	fun onPause() {
		bankServiceController.removeCallback(this)
		bankServiceController.disconnect()
	}

}