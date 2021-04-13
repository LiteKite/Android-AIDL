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
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.litekite.client.R
import com.litekite.client.app.ClientApp
import com.litekite.client.base.BaseActivity
import com.litekite.client.preference.PreferenceController
import com.litekite.client.signup.SignupActivity
import com.litekite.connector.controller.BankServiceConnector
import com.litekite.connector.controller.BankServiceController
import com.litekite.connector.entity.AuthResponse
import com.litekite.connector.entity.FailureResponse
import com.litekite.connector.entity.RequestCode
import com.litekite.connector.entity.ResponseCode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author Vignesh S
 * @version 1.0, 19/01/2020
 * @since 1.0
 */
@HiltViewModel
class LoginVM @Inject constructor(
    application: Application,
    private val bankServiceController: BankServiceController,
    private val preferenceController: PreferenceController
) : AndroidViewModel(application), LifecycleObserver, BankServiceConnector.Callback {

    companion object {
        val TAG: String = LoginVM::class.java.simpleName
    }

    val username: ObservableField<String> = ObservableField()
    val password: ObservableField<String> = ObservableField()

    private val applicationContext = getApplication() as ClientApp
    private val loginCompleted: MutableLiveData<Boolean> = MutableLiveData()

    fun isLoginCompletedBefore() =
        preferenceController.getBoolean(PreferenceController.PREFERENCE_LOGIN_COMPLETE_STATE)

    private fun storeLoginCompleted() =
        preferenceController.store(PreferenceController.PREFERENCE_LOGIN_COMPLETE_STATE, true)

    private fun storeLoggedInUserId(userId: Long) =
        preferenceController.store(PreferenceController.PREFERENCE_LOGGED_IN_USER_ID, userId)

    fun isLoginCompleted(): LiveData<Boolean> = loginCompleted

    fun onClick(v: View) {
        when (v.id) {
            R.id.b_login -> {
                if (TextUtils.isEmpty(username.get()) || TextUtils.isEmpty(password.get())) {
                    (v.context as BaseActivity).showSnackBar(
                        v.rootView,
                        R.string.err_all_fields_are_empty
                    )
                    return
                }
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
        if (authResponse.responseCode == ResponseCode.OK) {
            storeLoggedInUserId(authResponse.userId)
            storeLoginCompleted()
            loginCompleted.value = true
        }
    }

    override fun onFailureResponse(failureResponse: FailureResponse) {
        ClientApp.printLog(TAG, "onFailureResponse: ${failureResponse.responseCode}")
        if (failureResponse.requestCode == RequestCode.LOGIN) {
            when (failureResponse.responseCode) {
                ResponseCode.ERROR_LOG_IN_INCORRECT_USER_NAME_OR_PASSWORD -> {
                    ClientApp.showToast(
                        applicationContext,
                        R.string.err_incorrect_username_or_password
                    )
                }
                ResponseCode.ERROR_LOG_IN_USER_NOT_EXISTS -> {
                    ClientApp.showToast(applicationContext, R.string.err_login_user_not_exists)
                }
            }
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
