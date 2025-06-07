package com.andrea.imdbshowcase.network

import com.andrea.imdbshowcase.network.model.MovieResultsDto

interface TheMovieDataBaseApi {

    suspend fun getMovies(
        page: Int
    ): MovieResultsDto
}
