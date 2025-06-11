package com.andrea.tmdbshowcase.network

import com.andrea.tmdbshowcase.network.model.MovieDetailsDto
import com.andrea.tmdbshowcase.network.model.MovieResultsDto
import com.andrea.tmdbshowcase.network.utils.Helpers.handleErrors
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments

class TheMovieDataBaseApiImpl(private val client: HttpClient) : TheMovieDataBaseApi {
    override suspend fun getMovies(date: String, page: Int): MovieResultsDto {
        return handleErrors {
            client.get("3/discover/movie") {
                parameter("include_adult", "false")
                parameter("include_video", "false")
                parameter("language", "en-US")
                parameter("page", page)
                parameter("sort_by", "primary_release_date.desc")
                parameter("primary_release_date.lte", date)
            }.body()
        }
    }

    override suspend fun searchMovies(query: String): MovieResultsDto {
        return handleErrors {
            client.get("3/search/movie") {
                parameter("include_adult", "false")
                parameter("language", "en-US")
                parameter("query", query)
            }.body()
        }
    }

    override suspend fun getMovieDetail(movieId: String): MovieDetailsDto {
        return handleErrors {
            client
                .get("3") {
                    url { appendPathSegments("movie", movieId) }
                    parameter("language", "en-US")
                }
                .body()
        }
    }
}
