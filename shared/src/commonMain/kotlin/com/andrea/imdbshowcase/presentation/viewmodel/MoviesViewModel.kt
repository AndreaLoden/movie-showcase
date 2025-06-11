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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MoviesViewModel(
    private val movieRepository: MovieRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ViewModel() {

    private val moviesStateMutable = MutableStateFlow(MoviesState())
    val moviesState = moviesStateMutable.asStateFlow()

    internal val paginationStateMutable = MutableStateFlow(PaginationState())
    val paginationState = paginationStateMutable.asStateFlow()

    private val isRefreshMutable = MutableStateFlow(false)
    val isRefreshState: StateFlow<Boolean> = isRefreshMutable

    init {
        if (moviesStateMutable.value.movies.isEmpty()) {
            getMovies()
        }
    }

    private fun getMovies(
        page: Int = 1
    ) {
        val dateString = getTodaysDate()

        scope.launch {
            movieRepository.getMoviesRemote(dateString, page)
                .distinctUntilChanged()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> result.data?.let { data -> onRequestSuccess(data, dateString) }
                        is Resource.Error -> onRequestError(result.message)
                        is Resource.Loading -> onRequestLoading()
                    }
                }
        }
    }

    fun getTodaysDate(): String {
        val now: Instant = Clock.System.now()
        val date: LocalDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dateString = date.toString()

        return dateString
    }

    fun getMoviesPaginated() {
        if (moviesStateMutable.value.movies.isEmpty()) {
            return
        }

        if (paginationStateMutable.value.endReached) {
            return
        }

        getMovies(paginationStateMutable.value.page)
    }

    private fun onRequestSuccess(
        data: List<Movie>,
        dateString: String
    ) {
        val movies = moviesStateMutable.value.movies + data
        moviesStateMutable.update {
            it.copy(
                movies = movies,
                todaysDate = dateString,
                isLoading = false,
                error = ""
            )
        }

        paginationStateMutable.update {
            it.copy(
                page = it.page + 1,
                endReached = data.isEmpty(),
                isLoading = false
            )
        }
    }

    private fun onRequestError(
        message: String?
    ) {
        moviesStateMutable.update {
            it.copy(
                error = message ?: "Unexpected Error",
                isLoading = false
            )
        }
    }

    private fun onRequestLoading() {
        if (moviesStateMutable.value.movies.isEmpty()) {
            moviesStateMutable.update {
                it.copy(
                    isLoading = true
                )
            }
        }

        if (moviesStateMutable.value.movies.isNotEmpty()) {
            paginationStateMutable.update {
                it.copy(
                    isLoading = true
                )
            }
        }
    }
}
