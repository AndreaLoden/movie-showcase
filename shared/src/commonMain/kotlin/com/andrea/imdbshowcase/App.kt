@file:OptIn(ExperimentalMaterial3Api::class)

package com.andrea.imdbshowcase

import MoviesDetailScreen
import MoviesGridScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") { MoviesGridScreen(navController) }
                composable("detail/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: "N/A"
                    MoviesDetailScreen(id, navController)
                }
            }
        }
    }
}
