package com.andrea.tmdbshowcase.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIDeviceOrientationDidChangeNotification

private fun getCurrentOrientation(): Orientation {
    val orientation = UIDevice.currentDevice.orientation

    return if (
        orientation == UIDeviceOrientation.UIDeviceOrientationLandscapeLeft ||
        orientation == UIDeviceOrientation.UIDeviceOrientationLandscapeRight
    ) {
        Orientation.Landscape
    } else {
        Orientation.Portrait
    }
}

private fun observeOrientationFlow(): Flow<Orientation> = callbackFlow {
    // Emit current orientation immediately
    trySend(getCurrentOrientation())

    val observer = NSNotificationCenter.defaultCenter.addObserverForName(
        name = UIDeviceOrientationDidChangeNotification,
        `object` = null,
        queue = null
    ) { _: NSNotification? ->
        trySend(getCurrentOrientation())
    }

    UIDevice.currentDevice.beginGeneratingDeviceOrientationNotifications()

    awaitClose {
        UIDevice.currentDevice.endGeneratingDeviceOrientationNotifications()
        NSNotificationCenter.defaultCenter.removeObserver(observer)
    }
}

@Composable
actual fun orientationState(): State<Orientation> {
    val orientationFlow = remember { observeOrientationFlow() }
    return orientationFlow.collectAsState(initial = getCurrentOrientation())
}
