package com.andrea.imdbshowcase.network

import com.andrea.imdbshowcase.network.model.MovieResultsDto
import com.andrea.imdbshowcase.network.utils.Helpers
import com.vickbt.composeApp.data.network.models.MovieDetailsDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments

class TheMovieDataBaseApiImpl(private val client: HttpClient) : TheMovieDataBaseApi {
    override suspend fun getMovies(page: Int): MovieResultsDto {
        return Helpers.handleErrors {
            client.get("3/discover/movie") {
                parameter("include_adult", "false")
                parameter("include_video", "false")
                parameter("language", "en-US")
                parameter("page", page)
                parameter("sort_by", "primary_release_date.desc")
                parameter("primary_release_date.lte", "2025-06-06")
            }.body()
        }
    }

    override suspend fun getMovieDetail(movieId: String): MovieDetailsDto {
        return Helpers.handleErrors {
            client
                .get("3") {
                    url { appendPathSegments("movie", movieId) }
                    parameter("language", "en-US")
                }
                .body()
        }
    }
}
