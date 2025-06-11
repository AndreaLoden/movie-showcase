package com.andrea.imdbshowcase.presentation.ui

import MoviesSearchScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andrea.imdbshowcase.presentation.ui.screen.MoviesDetailScreen
import com.andrea.imdbshowcase.presentation.ui.screen.MoviesGridScreen

@Composable
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            contentAlignment = Alignment.Center
        ) {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable(
                    "home",
                    enterTransition = ::slideInToRight,
                    exitTransition = ::slideOutToLeft
                ) { MoviesGridScreen(navController) }

                composable(
                    "detail/{id}",
                    enterTransition = ::slideInToLeft,
                    exitTransition = ::slideOutToLeft,
                    popEnterTransition = ::slideInToRight,
                    popExitTransition = ::slideOutToRight
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: "N/A"
                    MoviesDetailScreen(id, navController)
                }

                composable(
                    "search",
                    enterTransition = ::slideInToLeft,
                    exitTransition = ::slideOutToLeft,
                    popEnterTransition = ::slideInToRight,
                    popExitTransition = ::slideOutToRight
                ) { MoviesSearchScreen(navController) }
            }
        }
    }
}

fun slideInToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(700)
    )
}

fun slideInToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return scope.slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(700)
    )
}

fun slideOutToLeft(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(700)
    )
}

fun slideOutToRight(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return scope.slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(700)
    )
}
