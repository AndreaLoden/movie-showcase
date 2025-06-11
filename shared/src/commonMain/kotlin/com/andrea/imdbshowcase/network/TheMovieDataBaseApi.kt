package com.andrea.imdbshowcase.network

import com.andrea.imdbshowcase.network.model.MovieDetailsDto
import com.andrea.imdbshowcase.network.model.MovieResultsDto

interface TheMovieDataBaseApi {

    suspend fun getMovies(
        date: String,
        page: Int
    ): MovieResultsDto

    suspend fun searchMovies(
        query: String,
        page: Int
    ): MovieResultsDto

    suspend fun getMovieDetail(
        movieId: String
    ): MovieDetailsDto
}
