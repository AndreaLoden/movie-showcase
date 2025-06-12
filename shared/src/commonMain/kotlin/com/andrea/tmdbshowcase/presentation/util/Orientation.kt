package com.andrea.tmdbshowcase.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

// shared/commonMain
enum class Orientation {
    Portrait, Landscape
}

// Composable orientation state
@Composable
expect fun orientationState(): State<Orientation>
