package com.andrea.imdbshowcase

import androidx.compose.ui.window.ComposeUIViewController
import com.andrea.imdbshowcase.presentation.ui.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
    }
) { App() }
