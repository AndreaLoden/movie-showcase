package com.andrea.imdbshowcase.di

import com.andrea.imdbshowcase.BuildConfig
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.network.MovieRepositoryImpl
import com.andrea.imdbshowcase.network.TheMovieDataBaseApi
import com.andrea.imdbshowcase.network.TheMovieDataBaseApiImpl
import com.andrea.imdbshowcase.presentation.viewmodel.MoviesViewModel
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
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val module = module {
    viewModel { MoviesViewModel(get()) }

    single<MovieRepository> { MovieRepositoryImpl(get()) }
    single<TheMovieDataBaseApi> { TheMovieDataBaseApiImpl(get()) }

    single {
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
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(module)
    }

// called by iOS
fun initKoinIos() = initKoin {}
