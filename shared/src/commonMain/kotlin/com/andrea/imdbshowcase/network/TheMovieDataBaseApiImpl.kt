package com.andrea.imdbshowcase.network

import com.andrea.imdbshowcase.network.model.MovieResultsDto
import com.andrea.imdbshowcase.network.utils.handleErrors
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder

class TheMovieDataBaseApiImpl(private val client: HttpClient) : TheMovieDataBaseApi {
    override suspend fun getMovies(page: Int): MovieResultsDto {
        return handleErrors {
            val url = URLBuilder("https://api.themoviedb.org/3/discover/movie").apply {
                parameters.append("include_adult", "false")
                parameters.append("include_video", "false")
                parameters.append("language", "en-US")
                parameters.append("page", page.toString())
                parameters.append("sort_by", "primary_release_date.desc")
            }.buildString()

            client.get(url).body()
        }
    }
}
