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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.nagopy.android.overlayviewmanager.OverlayViewManager
import com.nagopy.android.temporarybrightness.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var overlayViewManager: OverlayViewManager

    @Inject
    lateinit var handler: Handler

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_license -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            }
            R.id.menu_source_code -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/75py/Android-TemporaryBrightness")))
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
