package com.andrea.imdbshowcase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presentation.state.MoviesState
import com.andrea.imdbshowcase.presentation.state.PaginationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val movieRepository: MovieRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesState())
    val state = _state.asStateFlow()

    val _paginationState = MutableStateFlow(PaginationState())
    val paginationState = _paginationState.asStateFlow()

    private val _isRefresh = MutableStateFlow(false)
    val isRefresh: StateFlow<Boolean> = _isRefresh

    init {
        if (_state.value.movies.isEmpty()) {
            getMovies()
        }
    }

    private fun getMovies(
        page: Int = 1
    ) {
        scope.launch {
            movieRepository.getMoviesRemote(page = page)
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

    fun getMoviesPaginated() {
        if (_state.value.movies.isEmpty()) {
            return
        }

        if (_paginationState.value.endReached) {
            return
        }

        getMovies(_paginationState.value.page)
    }

    private fun onRequestSuccess(
        data: List<Movie>
    ) {
        val movies = _state.value.movies + data
        _state.update {
            it.copy(
                movies = movies,
                isLoading = false,
                error = ""
            )
        }

        _paginationState.update {
            it.copy(
                page = it.page + 1,
                endReached = data.isEmpty(),
                isLoading = false
            )
        }
    }

    internal fun onRequestError(
        message: String?
    ) {
        _state.update {
            it.copy(
                error = message ?: "Unexpected Error",
                isLoading = false
            )
        }
    }

    internal fun onRequestLoading() {
        if (_state.value.movies.isEmpty()) {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
        }

        if (_state.value.movies.isNotEmpty()) {
            _paginationState.update {
                it.copy(
                    isLoading = true
                )
            }
        }
    }

    fun updateState(
        isLoading: Boolean = false,
        movies: List<Movie> = emptyList(),
        error: String = ""
    ) {
        _state.update {
            it.copy(
                isLoading = isLoading,
                movies = movies,
                error = error
            )
        }
    }
}
