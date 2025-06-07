package com.andrea.imdbshowcase.core.repository

import com.andrea.imdbshowcase.core.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMoviesRemote(skip: Int): Flow<Resource<List<Movie>>>
}