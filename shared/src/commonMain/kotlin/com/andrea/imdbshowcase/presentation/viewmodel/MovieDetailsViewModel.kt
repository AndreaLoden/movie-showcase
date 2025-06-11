package com.andrea.imdbshowcase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presentation.state.MovieDetailsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val movieRepository: MovieRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ViewModel() {

    private val movieDetailsMutable = MutableStateFlow(MovieDetailsState())
    val movieDetailsState = movieDetailsMutable.asStateFlow()

    fun updateUiState(movieId: String) {
        if (movieId.isEmpty()) {
            return
        }
        getMovieDetails(movieId)
    }

    private fun getMovieDetails(movieId: String) {
        scope.launch {
            movieRepository.getMovieDetailsRemote(movieId)
                .distinctUntilChanged()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> result.data?.let { data -> onRequestSuccess(data) }
                        is Resource.Error -> onRequestError(result.message)
                        is Resource.Loading -> onRequestLoading()
                    }
                }
        }
    }

    private fun onRequestSuccess(
        movie: Movie
    ) {
        movieDetailsMutable.update {
            it.copy(
                movie = movie,
                isLoading = false,
                error = ""
            )
        }
    }

    private fun onRequestError(
        message: String?
    ) {
        movieDetailsMutable.update {
            it.copy(
                error = message ?: "Unexpected Error",
                isLoading = false
            )
        }
    }

    private fun onRequestLoading() {
        movieDetailsMutable.update {
            it.copy(isLoading = true)
        }
    }
}
