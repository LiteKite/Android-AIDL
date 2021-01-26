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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.litekite.client.R
import com.litekite.client.app.ClientApp
import com.litekite.client.base.BaseActivity
import com.litekite.client.databinding.ActivityLoginBinding
import com.litekite.client.home.HomeActivity
import com.litekite.client.preference.PreferenceController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @author Vignesh S
 * @version 1.0, 05/01/2021
 * @since 1.0
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity() {

	companion object {

		val TAG: String = LoginActivity::class.java.simpleName

		/**
		 * Launches LoginActivity.
		 *
		 * @param context An Activity Context.
		 */
		fun start(context: Context) {
			if (context is AppCompatActivity) {
				val intent = Intent(context, LoginActivity::class.java)
				context.startActivity(intent)
				startActivityAnimation(context)
			}
		}

	}

	@Inject
	lateinit var preferenceController: PreferenceController
	private lateinit var loginBinding: ActivityLoginBinding
	private val loginVM: LoginVM by viewModels()

	private val loginCompleteObserver = Observer<Boolean> { isCompleted ->
		if (isCompleted) {
			ClientApp.showToast(applicationContext, getString(R.string.login_success))
			startHomeActivity()
			finish()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (loginVM.isLoginCompletedBefore()) {
			ClientApp.printLog(TAG, "onCreate: already logged in. Going to Home!")
			startHomeActivity()
			finish()
			return
		}
		loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
		init()
	}

	private fun init() {
		loginBinding.presenter = loginVM
		lifecycle.addObserver(loginVM)
		loginVM.isLoginCompleted().observe(this, loginCompleteObserver)
	}

	private fun startHomeActivity() {
		HomeActivity.start(this)
	}

}