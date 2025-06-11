package com.andrea.imdbshowcase.di

import com.andrea.imdbshowcase.THEMOVIEDATABASE_API_KEY
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.network.MovieRepositoryImpl
import com.andrea.imdbshowcase.network.TheMovieDataBaseApi
import com.andrea.imdbshowcase.network.TheMovieDataBaseApiImpl
import com.andrea.imdbshowcase.presentation.viewmodel.MovieDetailsViewModel
import com.andrea.imdbshowcase.presentation.viewmodel.MovieGridViewModel
import com.andrea.imdbshowcase.presentation.viewmodel.MovieSearchViewModel
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
    viewModel { MovieGridViewModel(get()) }
    viewModel { MovieDetailsViewModel(get()) }
    viewModel { MovieSearchViewModel(get()) }

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
                    host = "api.themoviedb.org"
                    // host = "api.themoviedb.org/3" does not work on iOS
                    // https://stackoverflow.com/questions/77872206/ktor-kmp-request-failed-with-exception-kotlin-illegalstateexception-invalid-ur
                }
                header("accept", "application/json")
                header("Authorization", "Bearer $THEMOVIEDATABASE_API_KEY")
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
