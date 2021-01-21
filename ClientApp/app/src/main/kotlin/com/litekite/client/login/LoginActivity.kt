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

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.litekite.client.R
import com.litekite.client.app.ClientApp
import com.litekite.client.base.BaseActivity
import com.litekite.client.databinding.ActivityLoginBinding
import com.litekite.client.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Vignesh S
 * @version 1.0, 05/01/2021
 * @since 1.0
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity() {

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