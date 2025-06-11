package com.andrea.tmdbshowcase.presentation.viewmodel

import com.andrea.tmdbshowcase.core.model.Movie
import com.andrea.tmdbshowcase.core.repository.MovieRepository
import com.andrea.tmdbshowcase.core.repository.Resource
import com.andrea.tmdbshowcase.presentation.state.MovieSearchState
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MovieRepository
    private lateinit var viewModel: MovieSearchViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = MovieSearchViewModel(repository, CoroutineScope(testDispatcher))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Initial`() = runTest {
        assertEquals(MovieSearchState.Initial, viewModel.searchMovieResults.value)
    }

    @Test
    fun `onNewSearchQuery emits loading and then success when repository returns data`() = runTest {
        val movies = listOf(
            Movie("1", "url", "Title", "", "Overview", emptyList(), -1, emptyList(), 5.0, "2025")
        )

        everySuspend { repository.getMoviesForQueryRemote(any()) } returns flow {
            emit(Resource.Loading())
            emit(Resource.Success(movies))
        }

        val emittedStates = mutableListOf<MovieSearchState>()

        val job = launch {
            viewModel.searchMovieResults.collect {
                emittedStates.add(it)
            }
        }

        viewModel.onNewSearchQuery("test")

        advanceUntilIdle()

        assertTrue(emittedStates.first() is MovieSearchState.Initial)
        assertTrue(emittedStates.any { it is MovieSearchState.Loading })
        val resultState =
            emittedStates.find { it is MovieSearchState.Result } as MovieSearchState.Result
        assertEquals(movies, resultState.movies)

        job.cancel()
    }

    @Test
    fun `onNewSearchQuery emits loading and then error when repository returns error`() = runTest {
        val errorMsg = "Network error"

        everySuspend { repository.getMoviesForQueryRemote(any()) } returns flow {
            emit(Resource.Loading())
            emit(Resource.Error(errorMsg))
        }

        val emittedStates = mutableListOf<MovieSearchState>()

        val job = launch {
            viewModel.searchMovieResults.collect {
                emittedStates.add(it)
            }
        }

        viewModel.onNewSearchQuery("test")

        advanceUntilIdle()

        assertTrue(emittedStates.first() is MovieSearchState.Initial)
        assertTrue(emittedStates.any { it is MovieSearchState.Loading })
        val errorState =
            emittedStates.find { it is MovieSearchState.Error } as MovieSearchState.Error
        assertEquals(errorMsg, errorState.message)

        job.cancel()
    }

    @Test
    fun `onNewSearchQuery emits NoResults when repository returns empty list`() = runTest {
        everySuspend { repository.getMoviesForQueryRemote(any()) } returns flow {
            emit(Resource.Loading())
            emit(Resource.Success(emptyList()))
        }

        val emittedStates = mutableListOf<MovieSearchState>()

        val job = launch {
            viewModel.searchMovieResults.collect {
                emittedStates.add(it)
            }
        }

        viewModel.onNewSearchQuery("empty")

        advanceUntilIdle()

        assertTrue(emittedStates.first() is MovieSearchState.Initial)
        assertTrue(emittedStates.any { it is MovieSearchState.Loading })
        assertTrue(emittedStates.any { it is MovieSearchState.NoResults })

        job.cancel()
    }

    @Test
    fun `empty query resets to Initial state`() = runTest {
        everySuspend { repository.getMoviesForQueryRemote(any()) } returns flow {
            emit(Resource.Loading())
            emit(Resource.Success(emptyList()))
        }

        val emittedStates = mutableListOf<MovieSearchState>()

        val job = launch {
            viewModel.searchMovieResults.collect {
                emittedStates.add(it)
            }
        }

        viewModel.onNewSearchQuery("non-empty")

        advanceUntilIdle()

        // Then empty query
        viewModel.onNewSearchQuery("")
        advanceUntilIdle()

        // The last emitted state should be Initial (reset)
        assertTrue(emittedStates.last() is MovieSearchState.Initial)

        job.cancel()
    }
}
