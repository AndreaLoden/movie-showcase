package com.andrea.tmdbshowcase.presentation.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun orientationState(): State<Orientation> {
    val config = LocalConfiguration.current
    return remember(config.orientation) {
        mutableStateOf(
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Orientation.Landscape
            } else {
                Orientation.Portrait
            }
        )
    }
}
