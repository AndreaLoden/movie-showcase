package com.andrea.imdshowcase.network

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.network.MovieRepositoryImpl
import com.andrea.imdbshowcase.network.TheMovieDataBaseApi
import com.andrea.imdbshowcase.network.model.MovieDetailsDto
import com.andrea.imdbshowcase.network.model.MovieDto
import com.andrea.imdbshowcase.network.model.MovieResultsDto
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MovieRepositoryImplTest {

    private val testMovieDto = MovieDto(
        adult = false,
        backdropPath = "adsaddsa",
        genreIds = listOf(),
        id = 123456789,
        originalLanguage = "adsaddsa",
        originalTitle = "adsaddsa",
        overview = "adsaddsa",
        popularity = 5.0,
        posterPath = "adsaddsa",
        releaseDate = "adsaddsa",
        title = "Movie Title",
        video = false,
        voteAverage = 3.4,
        voteCount = 50,
        mediaType = "adsaddsa"
    )

    private val testMovieDetailsDto = MovieDetailsDto(
        adult = false,
        backdropPath = "adsaddsa",
        id = 123456789,
        originalLanguage = "adsaddsa",
        originalTitle = "adsaddsa",
        overview = "adsaddsa",
        popularity = 5.0,
        posterPath = "adsaddsa",
        releaseDate = "adsaddsa",
        title = "Movie Title",
        video = false,
        voteAverage = 3.4,
        voteCount = 50
    )

    private val testMovie = Movie(
        id = "123456789",
        imgURL = "https://image.tmdb.org/t/p/w500/adsaddsa",
        title = "Movie Title",
        tagline = "",
        overview = "adsaddsa",
        genres = listOf(),
        runtime = -1,
        spokenLanguages = listOf(),
        voteAverage = 3.4,
        releaseDate = "adsaddsa"
    )

    @Test
    fun `getMoviesRemote emits loading and success when API returns movies`() = runTest {
        // Arrange
        val fakeApi = mock<TheMovieDataBaseApi> {
            everySuspend { getMovies(any<Int>()) } returns MovieResultsDto(movies = listOf(testMovieDto))
        }

        val repository = MovieRepositoryImpl(fakeApi)

        // Act
        val results = repository.getMoviesRemote(page = 1).toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(listOf(testMovie), (results[1] as Resource.Success).data)
    }

    @Test
    fun `getMoviesRemote emits loading and error when API throws exception`() = runTest {
        // Arrange

        val fakeApi = mock<TheMovieDataBaseApi> {
            everySuspend { getMovies(any<Int>()) } throws RuntimeException("Network Error")
        }

        val repository = MovieRepositoryImpl(fakeApi)

        // Act
        val results = repository.getMoviesRemote(page = 1).toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals("Network Error", (results[1] as Resource.Error).message)
    }

    @Test
    fun `getMovieDetailsRemote emits loading and success when API returns movie details`() = runTest {
        // Arrange
        val fakeApi = mock<TheMovieDataBaseApi> {
            everySuspend { getMovieDetail("123456789") } returns testMovieDetailsDto
        }

        val repository = MovieRepositoryImpl(fakeApi)

        // Act
        val results = repository.getMovieDetailsRemote("123456789").toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(testMovie, (results[1] as Resource.Success).data)
    }

    @Test
    fun `getMovieDetailsRemote emits loading and error when API throws exception`() = runTest {
        // Arrange

        val fakeApi = mock<TheMovieDataBaseApi> {
            everySuspend { getMovieDetail(any<String>()) } throws RuntimeException("Network Error")
        }

        val repository = MovieRepositoryImpl(fakeApi)

        // Act
        val results = repository.getMovieDetailsRemote("123456789").toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals("Network Error", (results[1] as Resource.Error).message)
    }
}
