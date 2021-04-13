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
import android.view.MenuItem
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.litekite.client.R
import com.litekite.client.app.ClientApp
import com.litekite.client.base.BaseActivity
import com.litekite.client.login.LoginActivity
import com.litekite.client.preference.PreferenceController
import com.litekite.connector.controller.BankServiceConnector
import com.litekite.connector.controller.BankServiceController
import com.litekite.connector.entity.FailureResponse
import com.litekite.connector.entity.RequestCode
import com.litekite.connector.entity.ResponseCode
import com.litekite.connector.entity.UserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author Vignesh S
 * @version 1.0, 22/01/2020
 * @since 1.0
 */
@HiltViewModel
class HomeVM @Inject constructor(
    application: Application,
    private val bankServiceController: BankServiceController,
    private val preferenceController: PreferenceController
) : AndroidViewModel(application), LifecycleObserver, BankServiceConnector.Callback {

    companion object {
        val TAG: String = HomeVM::class.java.simpleName
    }

    val balance: ObservableField<String> = ObservableField()
    val amount: ObservableField<String> = ObservableField()
    val welcomeNote: ObservableField<String> = ObservableField()

    private val applicationContext = getApplication() as ClientApp

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
                bankServiceController.depositRequest(
                    getBankAccUserId(),
                    "${amount.get()}".toDouble()
                )
            }
            R.id.b_withdraw -> {
                if (TextUtils.isEmpty(amount.get())) {
                    (v.context as BaseActivity).showSnackBar(
                        v.rootView,
                        R.string.err_amount_empty
                    )
                    return
                }
                bankServiceController.withdrawRequest(
                    getBankAccUserId(),
                    "${amount.get()}".toDouble()
                )
            }
        }
    }

    private fun resetLoginCompleted() =
        preferenceController.store(PreferenceController.PREFERENCE_LOGIN_COMPLETE_STATE, false)

    private fun getBankAccUserId(): Long =
        preferenceController.getLong(PreferenceController.PREFERENCE_LOGGED_IN_USER_ID)

    private fun updateCurrentBalance(currentBalance: Double) =
        balance.set(String.format("%.2f", currentBalance))

    private fun clearAmount() = amount.set("")

    override fun onBankServiceConnected() {
        ClientApp.printLog(TAG, "onBankServiceConnected:")
        bankServiceController.userDetailsRequest(getBankAccUserId())
    }

    override fun onUserDetailsResponse(userDetails: UserDetails) {
        ClientApp.printLog(TAG, "onUserDetailsResponse:")
        updateCurrentBalance(userDetails.balance)
        welcomeNote.set(applicationContext.getString(R.string.welcome_note, userDetails.username))
    }

    override fun onCurrentBalanceChanged(currentBalance: Double) {
        ClientApp.printLog(TAG, "onCurrentBalanceChanged:")
        clearAmount()
        updateCurrentBalance(currentBalance)
    }

    override fun onFailureResponse(failureResponse: FailureResponse) {
        ClientApp.printLog(TAG, "failureResponse: ${failureResponse.responseCode}")
        clearAmount()
        if (failureResponse.requestCode == RequestCode.WITHDRAWAL ||
            failureResponse.requestCode == RequestCode.DEPOSIT
        ) {
            when (failureResponse.responseCode) {
                ResponseCode.ERROR_USER_NOT_FOUND -> {
                    ClientApp.showToast(applicationContext, R.string.err_user_not_found)
                }
                ResponseCode.ERROR_WITHDRAWAL_CURRENT_BALANCE_IS_ZERO -> {
                    ClientApp.showToast(applicationContext, R.string.err_withdrawal_balance_is_zero)
                }
                ResponseCode.ERROR_WITHDRAWAL_AMOUNT_EXCEEDS_CURRENT_BALANCE -> {
                    ClientApp.showToast(applicationContext, R.string.err_withdrawal_amount_exceeds)
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        bankServiceController.addCallback(this)
        if (bankServiceController.isServiceConnected()) {
            bankServiceController.userDetailsRequest(getBankAccUserId())
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        bankServiceController.removeCallback(this)
    }

    fun onOptionsItemSelected(activity: BaseActivity, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.logout -> {
                resetLoginCompleted()
                LoginActivity.start(activity)
                activity.finish()
                true
            }
            else -> activity.onOptionsItemSelected(item)
        }
    }
}
