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
import com.litekite.client.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Vignesh S
 * @version 1.0, 20/01/2020
 * @since 1.0
 */
@AndroidEntryPoint
class SignupActivity : BaseActivity() {

    companion object {

        /**
         * Launches SignupActivity.
         *
         * @param context An Activity Context.
         */
        fun start(context: Context) {
            if (context is AppCompatActivity) {
                val intent = Intent(context, SignupActivity::class.java)
                context.startActivity(intent)
                startActivityAnimation(context)
            }
        }
    }

    private lateinit var signupBinding: ActivitySignupBinding
    private val signupVM: SignupVM by viewModels()

    private val signupCompleteObserver = Observer<Boolean> { isCompleted ->
        if (isCompleted) {
            ClientApp.showToast(applicationContext, R.string.sign_up_success)
            onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        init()
    }

    private fun init() {
        setToolbar(
            signupBinding.tbWidget.toolbar,
            true,
            getString(R.string.sign_up),
            signupBinding.tbWidget.tvToolbarTitle
        )
        signupBinding.presenter = signupVM
        lifecycle.addObserver(signupVM)
        signupVM.isSignupCompleted().observe(this, signupCompleteObserver)
    }
}
