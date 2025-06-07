package com.andrea.imdbshowcase.network

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.network.model.toMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepositoryImpl(
    private val theMovieDataBaseApi: TheMovieDataBaseApi,
) : MovieRepository {

    override fun getMoviesRemote(skip: Int): Flow<Resource<List<Movie>>> {

        return flow {
            try {
                emit(Resource.Loading())
                val movies = theMovieDataBaseApi
                    .getMovies(page = skip)
                    .movies
                    .map { it.toMovie() }

                emit(Resource.Success(movies))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unexpected Error"))
            }
        }
    }

}