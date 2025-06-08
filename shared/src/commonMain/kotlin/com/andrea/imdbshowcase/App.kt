package com.andrea.imdbshowcase

import MoviesGridScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andrea.imdbshowcase.network.MovieRepositoryImpl
import IMDB_Showcase.shared.BuildConfig
import com.andrea.imdbshowcase.network.TheMovieDataBaseApiImpl
import com.andrea.imdbshowcase.presentation.movies.MoviesViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Composable
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MoviesGridScreen(
                MoviesViewModel(
                    MovieRepositoryImpl(
                        TheMovieDataBaseApiImpl(
                            HttpClient {
                                install(ContentNegotiation) {
                                    json(Json { ignoreUnknownKeys = true })
                                }

                                install(HttpCache)
                                install(Logging) {
                                    level = LogLevel.ALL
                                }

                                install(DefaultRequest) {
                                    url {
                                        protocol = URLProtocol.HTTPS
                                        host = "api.themoviedb.org/3"
                                    }
                                    header("accept", "application/json")
                                    header("Authorization", "Bearer ${BuildConfig.THEMOVIEDATABASE_API_KEY}")
                                }
                            }
                        )
                    )
                )
            )
        }
    }
}
