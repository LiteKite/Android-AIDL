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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.litekite.client.R
import com.litekite.client.base.BaseActivity
import com.litekite.client.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Vignesh S
 * @version 1.0, 21/01/2021
 * @since 1.0
 */
@AndroidEntryPoint
class HomeActivity : BaseActivity() {

	companion object {

		/**
		 * Launches HomeActivity.
		 *
		 * @param context An Activity Context.
		 */
		fun start(context: Context) {
			if (context is AppCompatActivity) {
				val intent = Intent(context, HomeActivity::class.java)
				context.startActivity(intent)
				startActivityAnimation(context)
			}
		}

	}

	private lateinit var homeBinding: ActivityHomeBinding
	private val homeVM: HomeVM by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
		init()
	}

	private fun init() {
		setToolbar(
			homeBinding.tbWidget.toolbar,
			false,
			getString(R.string.home),
			homeBinding.tbWidget.tvToolbarTitle
		)
		homeBinding.presenter = homeVM
		lifecycle.addObserver(homeVM)
	}

	override fun getToolbarMenuResource() = R.menu.home_menu

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		return homeVM.onOptionsItemSelected(this, item)
	}

}