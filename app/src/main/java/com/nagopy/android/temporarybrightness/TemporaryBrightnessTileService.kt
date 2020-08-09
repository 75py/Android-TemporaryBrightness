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
import android.service.quicksettings.Tile.*
import android.service.quicksettings.TileService
import com.nagopy.android.overlayviewmanager.OverlayViewManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TemporaryBrightnessTileService : TileService() {

    @Inject
    lateinit var overlayViewManager: OverlayViewManager

    @Inject
    lateinit var brightnessOverride: BrightnessOverride

    override fun onClick() {
        Timber.d("onClick")
        val tile = qsTile

        when (tile.state) {
            STATE_ACTIVE -> {
                brightnessOverride.stopOverride()
                tile.state = STATE_INACTIVE
            }
            STATE_INACTIVE -> {
                brightnessOverride.startOverride()
                tile.state = STATE_ACTIVE

                // Close the Quick Settings panel
                startActivityAndCollapse(Intent(this, DummyActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }

        tile.updateTile()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        Timber.d("onTileAdded")
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        Timber.d("onTileRemoved")
    }

    override fun onStartListening() {
        super.onStartListening()
        Timber.d("onStartListening")
        updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        Timber.d("onStopListening")
    }

    private fun updateTile() {
        val tile = qsTile
        if (overlayViewManager.canDrawOverlays()) {
            if (brightnessOverride.isEnabled()) {
                tile.state = STATE_ACTIVE
            } else {
                tile.state = STATE_INACTIVE
            }
        } else {
            tile.state = STATE_UNAVAILABLE
        }
        tile.updateTile()
    }
}
