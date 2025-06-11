package com.andrea.imdbshowcase.presentation.viewmodel

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presentation.state.MovieGridUiState
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
class MovieGridViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: MovieRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mock()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val testMovie = Movie(
        id = "1",
        imgURL = "an-img-url",
        title = "A Beautiful Title",
        tagline = "adsaddsa",
        overview = "adsaddsa",
        genres = listOf(),
        runtime = 120,
        spokenLanguages = listOf(),
        voteAverage = 3.4,
        releaseDate = "adsaddsa"
    )

    @Test
    fun `initial state should be Loading then Success on success`() = runTest {
        val fakeRepo = mock<MovieRepository> {
            everySuspend { getMoviesRemote(any(), any()) } returns flow {
                emit(Resource.Loading())
                emit(Resource.Success(listOf(testMovie)))
            }
        }

        val movieGridViewModel = MovieGridViewModel(fakeRepo, this)

        advanceUntilIdle()

        val uiState = movieGridViewModel.moviesState.value
        val paginationState = movieGridViewModel.paginationState.value

        assertTrue(uiState is MovieGridUiState.Success)
        assertEquals(listOf(testMovie), uiState.movies)
        assertEquals(2, paginationState.page)
    }

    @Test
    fun `initial state should be Error on failure`() = runTest {
        val fakeRepo = mock<MovieRepository> {
            everySuspend { getMoviesRemote(any(), any()) } returns flow {
                emit(Resource.Loading())
                emit(Resource.Error("Failed"))
            }
        }

        val movieGridViewModel = MovieGridViewModel(fakeRepo, this)

        advanceUntilIdle()

        val uiState = movieGridViewModel.moviesState.value
        assertTrue(uiState is MovieGridUiState.Error)
        assertEquals("Failed", uiState.message)
    }

    @Test
    fun `pagination triggers new page load if not loading or ended`() = runTest {
        val fakeRepo = mock<MovieRepository> {
            everySuspend { getMoviesRemote(any(), any()) } returns flow {
                emit(Resource.Loading())
                emit(Resource.Success(listOf(testMovie)))
            }
        }

        val movieGridViewModel = MovieGridViewModel(fakeRepo, this)

        advanceUntilIdle()

        val previousPage = movieGridViewModel.paginationState.value.page
        movieGridViewModel.getMoviesPaginated()

        advanceUntilIdle()

        val newPage = movieGridViewModel.paginationState.value.page
        assertTrue(newPage > previousPage)
    }
}
