package com.andrea.imdbshowcase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presentation.state.MovieDetailState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieRepository: MovieRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ViewModel() {

    private val _movieDetailsState = MutableStateFlow(MovieDetailState())
    val movieDetailsState = _movieDetailsState.asStateFlow()

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
        _movieDetailsState.update {
            it.copy(
                movie = movie,
                isLoading = false,
                error = ""
            )
        }
    }

    internal fun onRequestError(
        message: String?
    ) {
        _movieDetailsState.update {
            it.copy(
                error = message ?: "Unexpected Error",
                isLoading = false
            )
        }
    }

    internal fun onRequestLoading() {
        _movieDetailsState.update {
            it.copy(isLoading = true)
        }
    }
}
