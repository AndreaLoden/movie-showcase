package com.andrea.imdbshowcase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presentation.state.MovieGridPaginationState
import com.andrea.imdbshowcase.presentation.state.MovieGridUiState
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MovieGridViewModel(
    private val movieRepository: MovieRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ViewModel() {

    private val moviesStateMutable = MutableStateFlow<MovieGridUiState>(MovieGridUiState.Loading)
    val moviesState: StateFlow<MovieGridUiState> = moviesStateMutable.asStateFlow()

    private val paginationStateMutable = MutableStateFlow(MovieGridPaginationState())
    val paginationState: StateFlow<MovieGridPaginationState> = paginationStateMutable.asStateFlow()

    init {
        getMovies()
    }

    private fun getTodaysDate(): String {
        val now = Clock.System.now()
        val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return date.toString()
    }

    private fun getMovies(page: Int = 1) {
        val dateString = getTodaysDate()

        scope.launch {
            movieRepository.getMoviesRemote(dateString, page)
                .distinctUntilChanged()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> onRequestLoading()
                        is Resource.Success -> result.data?.let { onRequestSuccess(it, dateString) }
                        is Resource.Error -> onRequestError(result.message)
                    }
                }
        }
    }

    fun getMoviesPaginated() {
        val paginationState = paginationStateMutable.value

        if (paginationState.isLoading || paginationState.endReached) return

        getMovies(paginationState.page)
    }

    private fun onRequestLoading() {
        // Initial loading
        if (moviesStateMutable.value is MovieGridUiState.Success) {
            paginationStateMutable.update { it.copy(isLoading = true) }
        } else {
            moviesStateMutable.value = MovieGridUiState.Loading
        }
    }

    private fun onRequestSuccess(movies: List<Movie>, dateString: String) {
        val currentMovies = when (val state = moviesStateMutable.value) {
            is MovieGridUiState.Success -> state.movies
            else -> emptyList()
        }

        val updatedMovies = (currentMovies + movies).distinctBy { it.id }

        moviesStateMutable.value = MovieGridUiState.Success(
            movies = updatedMovies,
            todaysDate = dateString
        )

        paginationStateMutable.update {
            it.copy(
                page = it.page + 1,
                endReached = movies.isEmpty(),
                isLoading = false
            )
        }
    }

    private fun onRequestError(message: String?) {
        moviesStateMutable.value = MovieGridUiState.Error(
            message = message ?: "Unexpected Error"
        )

        paginationStateMutable.update { it.copy(isLoading = false) }
    }
}
