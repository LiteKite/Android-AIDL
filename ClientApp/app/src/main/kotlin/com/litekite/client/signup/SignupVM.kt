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
package com.litekite.client.signup

import android.app.Application
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.litekite.client.R
import com.litekite.client.app.ClientApp
import com.litekite.client.base.BaseActivity
import com.litekite.connector.controller.BankServiceConnector
import com.litekite.connector.controller.BankServiceController
import com.litekite.connector.entity.AuthResponse
import com.litekite.connector.entity.FailureResponse
import com.litekite.connector.entity.RequestCode
import com.litekite.connector.entity.ResponseCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author Vignesh S
 * @version 1.0, 19/01/2020
 * @since 1.0
 */
@HiltViewModel
class SignupVM @Inject constructor(
    application: Application,
    private val bankServiceController: BankServiceController
) : AndroidViewModel(application), LifecycleObserver, BankServiceConnector.Callback {

    companion object {
        val TAG: String = SignupVM::class.java.simpleName
    }

    val username: ObservableField<String> = ObservableField()
    val password: ObservableField<String> = ObservableField()
    val confirmPassword: ObservableField<String> = ObservableField()

    private val applicationContext = getApplication() as ClientApp
    private val _signupCompleted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val signupCompleted: StateFlow<Boolean> = _signupCompleted

    fun onClick(v: View) {
        when (v.id) {
            R.id.b_signup -> {
                if (TextUtils.isEmpty(username.get()) ||
                    TextUtils.isEmpty(password.get()) ||
                    TextUtils.isEmpty(confirmPassword.get())
                ) {
                    (v.context as BaseActivity).showSnackBar(
                        v.rootView,
                        R.string.err_all_fields_are_empty
                    )
                    return
                }
                if (password.get() != confirmPassword.get()) {
                    (v.context as BaseActivity).showSnackBar(
                        v.rootView,
                        R.string.err_incorrect_confirm_password
                    )
                    return
                }
                bankServiceController.signup("${username.get()}", "${password.get()}")
            }
        }
    }

    override fun onFailureResponse(failureResponse: FailureResponse) {
        ClientApp.printLog(TAG, "onFailureResponse: ${failureResponse.responseCode}")
        if (failureResponse.requestCode == RequestCode.SIGNUP) {
            if (failureResponse.responseCode == ResponseCode.ERROR_SIGN_UP_USER_EXISTS) {
                ClientApp.showToast(applicationContext, R.string.err_user_already_exists)
            }
        }
    }

    override fun onSignupResponse(authResponse: AuthResponse) {
        super.onSignupResponse(authResponse)
        ClientApp.printLog(TAG, "onSignupResponse:")
        if (authResponse.responseCode == ResponseCode.OK) {
            _signupCompleted.value = true
        }
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
