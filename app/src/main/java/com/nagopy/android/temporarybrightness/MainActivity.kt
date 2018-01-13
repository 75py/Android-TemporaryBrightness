/*
 * Copyright 2018 75py
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

package com.nagopy.android.temporarybrightness

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.nagopy.android.overlayviewmanager.OverlayViewManager
import com.nagopy.android.temporarybrightness.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val injector = KodeinInjector()
    val overlayViewManager: OverlayViewManager by injector.instance()
    val handler: Handler by injector.instance()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(appKodein())

        if (!overlayViewManager.canDrawOverlays()) {
            overlayViewManager.showPermissionRequestDialog(supportFragmentManager, R.string.app_name)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.canDrawOverlays = overlayViewManager.canDrawOverlays()
        binding.expandHowToSetUp = true
        binding.expandHowToUse = true
        binding.maxImageWidth = overlayViewManager.displayWidth * 2 / 3
    }

    override fun onRestart() {
        super.onRestart()
        handler.postDelayed({
            binding.canDrawOverlays = overlayViewManager.canDrawOverlays()
        }, 500)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_requestPermission -> overlayViewManager.requestOverlayPermission()
        }
    }

}
