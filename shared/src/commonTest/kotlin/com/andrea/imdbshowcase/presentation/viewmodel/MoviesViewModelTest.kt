package com.andrea.imdbshowcase.presentation.viewmodel

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presentation.state.PaginationState
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

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

    private fun createMovie(id: String, title: String, imgURL: String = "") =
        Movie(id, imgURL, title)

    @Test
    fun `init loads movies`() = runTest {
        val movie = createMovie("1", "Test Movie")
        every { repo.getMoviesRemote(1) } returns flowOf(Resource.Success(listOf(movie)))

        val viewModel = MoviesViewModel(repo, this)

        advanceUntilIdle()

        assertEquals(listOf(movie), viewModel.state.value.movies)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals("", viewModel.state.value.error)
    }

    @Test
    fun `getMoviesPaginated does nothing when no movies`() = runTest {
        every { repo.getMoviesRemote(any()) } returns flowOf(Resource.Success(emptyList()))

        val viewModel = MoviesViewModel(repo, this)

        viewModel.updateState(movies = emptyList())

        viewModel.getMoviesPaginated()

        advanceUntilIdle()

        assertTrue(viewModel.state.value.movies.isEmpty())
    }

    @Test
    fun `getMoviesPaginated fetches next page`() = runTest {
        val firstPage = listOf(createMovie("1", "Movie 1"))
        val secondPage = listOf(createMovie("2", "Movie 2"))

        every { repo.getMoviesRemote(1) } returns flowOf(Resource.Success(firstPage))
        every { repo.getMoviesRemote(2) } returns flowOf(Resource.Success(secondPage))

        val viewModel = MoviesViewModel(repo, this)

        advanceUntilIdle()
        assertEquals(firstPage, viewModel.state.value.movies)

        // Manually update pagination state for the next page
        viewModel._paginationState.value = PaginationState(skip = 2, endReached = false)

        viewModel.getMoviesPaginated()

        advanceUntilIdle()

        assertEquals(firstPage + secondPage, viewModel.state.value.movies)
    }

    @Test
    fun `onRequestError updates error state`() = runTest {
        every { repo.getMoviesRemote(1) } returns flowOf(Resource.Error("Network failure"))

        val viewModel = MoviesViewModel(repo, this)

        advanceUntilIdle()

        assertEquals("Network failure", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onRequestLoading sets loading state correctly`() = runTest {
        every { repo.getMoviesRemote(1) } returns flowOf(Resource.Loading())

        val viewModel = MoviesViewModel(repo, this)

        advanceUntilIdle()

        assertTrue(viewModel.state.value.isLoading)
    }
}
