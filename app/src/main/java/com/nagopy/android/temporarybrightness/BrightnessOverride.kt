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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.view.LayoutInflater
import android.widget.SeekBar
import com.nagopy.android.overlayviewmanager.OverlayViewManager
import com.nagopy.android.temporarybrightness.databinding.ViewOverrideBinding
import timber.log.Timber
import javax.inject.Inject

class BrightnessOverride @Inject constructor(
        private val context: Context,
        private val userSettings: UserSettings,
        private val handler: Handler,
        private val overlayViewManager: OverlayViewManager
) : SeekBar.OnSeekBarChangeListener {

    private val binding = ViewOverrideBinding.inflate(LayoutInflater.from(context)).apply {
        onSeekBarChangeListener = this@BrightnessOverride
        brightness = userSettings.getOverrideBrightness()
    }

    val overlayView = overlayViewManager.newOverlayView(binding.root)
            .setWidth(overlayViewManager.displayWidth)
            .setTouchable(true)
            .setAlpha(0.8f)
            .setScreenBrightness(binding.brightness / 255f)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopOverride()
        }
    }

    private var isTouchingSeekBar = false

    fun startOverride() {
        isTouchingSeekBar = false
        overlayView.setAlpha(0.8f)
                .setTouchable(true)
                .setWidth(overlayViewManager.displayWidth)
                .show()
        overlayView.view.alpha = 1f
        handler.postDelayed({
            if (overlayView.isVisible && !isTouchingSeekBar) {
                startAlphaAnimation(500, 0)
            }
        }, 5000)
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    fun stopOverride() {
        overlayView.hide()
        context.unregisterReceiver(receiver)
    }

    fun isEnabled() = overlayView.isVisible

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        overlayView.setScreenBrightness(progress / 255f).update()
        userSettings.updateOverrideBrightness(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        Timber.d("onStartTrackingTouch")
        isTouchingSeekBar = true
        overlayView.view.animate().cancel()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        Timber.d("onStopTrackingTouch")
        isTouchingSeekBar = false
        startAlphaAnimation(500, 250)
    }

    private fun startAlphaAnimation(duration: Long, delay: Long) {
        overlayView.setTouchable(false).update()
        overlayView.view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animator: Animator) {
                        overlayView.setAlpha(0f).update()
                    }
                })
                .setStartDelay(delay)
                .start()
    }

}
