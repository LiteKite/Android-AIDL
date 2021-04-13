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
package com.litekite.client.base

import android.content.Context
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.litekite.client.R

/**
 * BaseActivity, Provides common features and functionality available for all activities.
 *
 * @author Vignesh S
 * @version 1.0, 03/06/2020
 * @since 1.0
 */
@Suppress("REGISTERED")
open class BaseActivity : AppCompatActivity() {

    companion object {

        /**
         * Starts Activity animation.
         *
         * @param context An activity context.
         */
        fun startActivityAnimation(context: Context) {
            if (context is AppCompatActivity) {
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    /**
     * Sets Toolbar, Toolbar Title and Back Navigation Button.
     *
     * @param toolbar        Toolbar widget.
     * @param backBtnVisible A boolean value whether to display Toolbar back navigation button or
     * not.
     * @param toolbarTitle   The title of a Toolbar.
     * @param tvToolbarTitle A TextView in which the title of a Toolbar is displayed.
     */
    protected fun setToolbar(
        toolbar: Toolbar,
        backBtnVisible: Boolean,
        toolbarTitle: String,
        tvToolbarTitle: TextView
    ) {
        toolbar.title = ""
        if (backBtnVisible) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        }
        toolbar.setContentInsetsAbsolute(0, 0)
        tvToolbarTitle.text = toolbarTitle
        setSupportActionBar(toolbar)
        if (backBtnVisible) {
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    /**
     * @param v           A View in which the SnackBar should be displayed at the bottom of the
     * screen.
     * @param stringResID A message that to be displayed inside a SnackBar.
     */
    fun showSnackBar(v: View, @StringRes stringResID: Int) {
        Snackbar.make(v, stringResID, Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (getToolbarMenuResource() == 0) {
            super.onCreateOptionsMenu(menu)
        } else {
            menuInflater.inflate(getToolbarMenuResource(), menu)
            true
        }
    }

    open fun getToolbarMenuResource(): Int = 0
}
