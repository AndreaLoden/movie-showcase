package com.andrea.imdbshowcase.presenttion.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presenttion.movies.state.MoviesState
import com.andrea.imdbshowcase.presenttion.movies.state.PaginationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesState())
    val state = _state.asStateFlow()

    private val _paginationState = MutableStateFlow(PaginationState())
    val paginationState = _paginationState.asStateFlow()

    private val _isRefresh = MutableStateFlow(false)
    val isRefresh: StateFlow<Boolean> = _isRefresh

    init {
        if (_state.value.movies.isEmpty()) {
            getMovies()
        }
    }

    private fun getMovies(
        skip: Int = 1
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.getMoviesRemote(skip = skip)
                .distinctUntilChanged()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> result.data?.let { data -> onRequestSuccess(data) }
                        is Resource.Error -> onRequestError(result.message)
                        is Resource.Loading -> onRequestLoading()
                        else -> Unit
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

        getMovies(_paginationState.value.skip)
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

        val listSize = _state.value.movies.size
        _paginationState.update {
            it.copy(
                skip = it.skip + 1,
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

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            updateRefreshState(true)
            _paginationState.update { it.copy(skip = 0) }
            _state.update { it }
            getMovies()
            updateRefreshState(false)
        }
    }
    private fun updateRefreshState(
        value: Boolean
    ) = _isRefresh.update { value }

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
