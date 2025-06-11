package com.andrea.tmdbshowcase

import androidx.compose.ui.window.ComposeUIViewController
import com.andrea.tmdbshowcase.presentation.ui.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
    }
) { App() }
