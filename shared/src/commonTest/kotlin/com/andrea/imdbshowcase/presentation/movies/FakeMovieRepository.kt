package com.andrea.imdbshowcase.presentation.movies

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeMovieRepository(
    private val responses: Map<Int, Flow<Resource<List<Movie>>>>
) : MovieRepository {
    override fun getMoviesRemote(skip: Int): Flow<Resource<List<Movie>>> {
        return responses[skip] ?: flowOf(Resource.Success(emptyList()))
    }
}
