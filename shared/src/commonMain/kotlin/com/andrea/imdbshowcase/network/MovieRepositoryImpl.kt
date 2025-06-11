package com.andrea.imdbshowcase.network

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.network.model.toMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepositoryImpl(
    private val theMovieDataBaseApi: TheMovieDataBaseApi
) : MovieRepository {

    override fun getMoviesRemote(date: String, page: Int): Flow<Resource<List<Movie>>> {
        return flow {
            try {
                emit(Resource.Loading())
                val movies = theMovieDataBaseApi
                    .getMovies(date, page)
                    .movies
                    .map { it.toMovie() }
                    .filter {
                        // Could be moved to a use case or function?
                        it.title.isNotEmpty() && it.imgURL.isNotEmpty()
                    }

                emit(Resource.Success(movies))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unexpected Error"))
            }
        }
    }

    override fun getMoviesForQueryRemote(query: String): Flow<Resource<List<Movie>>> {
        return flow {
            try {
                emit(Resource.Loading())
                val movies = theMovieDataBaseApi
                    .searchMovies(query)
                    .movies
                    .map { it.toMovie() }
                    .filter {
                        // Could be moved to a use case or function?
                        it.title.isNotEmpty() && it.imgURL.isNotEmpty()
                    }

                emit(Resource.Success(movies))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unexpected Error"))
            }
        }
    }

    override fun getMovieDetailsRemote(movieId: String): Flow<Resource<Movie>> {
        return flow {
            try {
                emit(Resource.Loading())
                val movies = theMovieDataBaseApi
                    .getMovieDetail(movieId)
                    .toMovie()

                emit(Resource.Success(movies))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unexpected Error"))
            }
        }
    }
}
