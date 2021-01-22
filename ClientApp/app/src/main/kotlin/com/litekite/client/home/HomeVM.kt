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

package com.litekite.client.home

import android.app.Application
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.litekite.client.R
import com.litekite.client.app.ClientApp
import com.litekite.client.base.BaseActivity
import com.litekite.client.preference.PreferenceController
import com.litekite.connector.controller.BankServiceConnector
import com.litekite.connector.controller.BankServiceController
import com.litekite.connector.entity.FailureResponse

/**
 * @author Vignesh S
 * @version 1.0, 22/01/2020
 * @since 1.0
 */
class HomeVM @ViewModelInject constructor(
	application: Application,
	private val bankServiceController: BankServiceController,
	private val preferenceController: PreferenceController
) : AndroidViewModel(application), LifecycleObserver, BankServiceConnector.Callback {

	companion object {
		val TAG: String = HomeVM::class.java.simpleName
	}

	val amount: ObservableField<String> = ObservableField()

	fun onClick(v: View) {
		when (v.id) {
			R.id.b_deposit -> {
				if (TextUtils.isEmpty(amount.get())) {
					(v.context as BaseActivity).showSnackBar(
						v.rootView,
						R.string.err_amount_empty
					)
					return
				}
			}
			R.id.b_withdraw -> {
				if (TextUtils.isEmpty(amount.get())) {
					(v.context as BaseActivity).showSnackBar(
						v.rootView,
						R.string.err_amount_empty
					)
					return
				}
			}
		}
	}

	override fun onFailureResponse(failureResponse: FailureResponse) {
		ClientApp.printLog(TAG, "failureResponse: ${failureResponse.responseCode}")
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	fun onCreate() {
		bankServiceController.addCallback(this)
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	fun onDestroy() {
		bankServiceController.removeCallback(this)
	}

}