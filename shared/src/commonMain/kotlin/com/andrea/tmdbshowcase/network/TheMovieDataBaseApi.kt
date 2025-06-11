package com.andrea.tmdbshowcase.network

import com.andrea.tmdbshowcase.network.model.MovieDetailsDto
import com.andrea.tmdbshowcase.network.model.MovieResultsDto

interface TheMovieDataBaseApi {

    suspend fun getMovies(
        date: String,
        page: Int
    ): MovieResultsDto

    suspend fun searchMovies(
        query: String
    ): MovieResultsDto

    suspend fun getMovieDetail(
        movieId: String
    ): MovieDetailsDto
}
