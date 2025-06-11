package com.andrea.imdbshowcase.core.repository

import com.andrea.imdbshowcase.core.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMoviesRemote(date: String, page: Int): Flow<Resource<List<Movie>>>
    fun getMoviesForQueryRemote(query: String): Flow<Resource<List<Movie>>>
    fun getMovieDetailsRemote(movieId: String): Flow<Resource<Movie>>
}
