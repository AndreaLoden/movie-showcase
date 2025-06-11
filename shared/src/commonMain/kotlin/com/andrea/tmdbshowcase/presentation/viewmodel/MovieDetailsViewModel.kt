package com.andrea.tmdbshowcase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.andrea.tmdbshowcase.core.repository.MovieRepository
import com.andrea.tmdbshowcase.core.repository.Resource
import com.andrea.tmdbshowcase.presentation.state.MovieDetailState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val movieRepository: MovieRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ViewModel() {

    private val movieDetailsMutable = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
    val movieDetailsState = movieDetailsMutable.asStateFlow()

    fun loadMovieWithId(movieId: String) {
        getMovieDetails(movieId)
    }

    private fun getMovieDetails(movieId: String) {
        scope.launch {
            movieRepository.getMovieDetailsRemote(movieId)
                .distinctUntilChanged()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> movieDetailsMutable.value = MovieDetailState.Loading
                        is Resource.Success -> {
                            result.data?.let {
                                movieDetailsMutable.value = MovieDetailState.Success(it)
                            }
                        }

                        is Resource.Error -> {
                            movieDetailsMutable.value = MovieDetailState.Error(
                                result.message ?: "Unexpected Error"
                            )
                        }
                    }
                }
        }
    }
}
