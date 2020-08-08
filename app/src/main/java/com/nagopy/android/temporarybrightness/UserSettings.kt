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

import android.content.SharedPreferences

class UserSettings(private val sharedPreferences: SharedPreferences) {

    fun getOverrideBrightness() = sharedPreferences.getInt(KEY_OVERRIDE_BRIGHTNESS, 192)

    fun updateOverrideBrightness(value: Int) {
        sharedPreferences.edit().putInt(KEY_OVERRIDE_BRIGHTNESS, value).apply()
    }

    companion object {
        const val KEY_OVERRIDE_BRIGHTNESS = "OVERRIDE_BRIGHTNESS"
    }
}