package com.andrea.imdbshowcase.network

import com.andrea.imdbshowcase.network.model.MovieResultsDto
import com.vickbt.composeApp.data.network.models.MovieDetailsDto

interface TheMovieDataBaseApi {

    suspend fun getMovies(
        page: Int
    ): MovieResultsDto

    suspend fun getMovieDetail(
        movieId: String
    ): MovieDetailsDto
}
