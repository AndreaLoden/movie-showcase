import com.andrea.imdbshowcase.network.TheMovieDataBaseApiImpl
import com.andrea.imdbshowcase.network.utils.TheMovieDataBaseError
import com.andrea.imdbshowcase.network.utils.TheMovieDataBaseException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TheMovieDataBaseApiImplTest {

    private fun createMockClient(
        expectedPathSegments: List<String>,
        expectedParameters: Map<String, String>,
        responseJson: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ): HttpClient {
        val mockEngine = MockEngine { request ->
            // Validate path segments
            val pathSegments = request.url.encodedPath.trim('/').split('/')
            assertEquals(expectedPathSegments, pathSegments)

            // Validate parameters
            expectedParameters.forEach { (key, value) ->
                assertEquals(value, request.url.parameters[key])
            }

            respond(
                content = responseJson,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    // -- Tests for getMovies --

    @Test
    fun `getMovies returns valid data`() = runTest {
        val expectedPath = listOf("3", "discover", "movie")
        val expectedParams = mapOf(
            "include_adult" to "false",
            "include_video" to "false",
            "language" to "en-US",
            "page" to "1",
            "sort_by" to "primary_release_date.desc",
            "primary_release_date.lte" to "2025-06-06"
        )

        val responseJson = """
            {
              "page": 1,
              "results": [
                { "id": 1 }
              ],
              "total_pages": 1,
              "total_results": 1
            }
        """.trimIndent()

        val client = createMockClient(expectedPath, expectedParams, responseJson)
        val api = TheMovieDataBaseApiImpl(client)

        val result = api.getMovies(1)

        assertEquals(1, result.page)
        assertEquals(1, result.movies.size)
        assertEquals(1, result.movies.first().id)
    }

    @Test
    fun `getMovies throws client error on 404`() = runTest {
        val expectedPath = listOf("3", "discover", "movie")
        val expectedParams = mapOf(
            "include_adult" to "false",
            "include_video" to "false",
            "language" to "en-US",
            "page" to "1",
            "sort_by" to "primary_release_date.desc",
            "primary_release_date.lte" to "2025-06-06"
        )

        val client =
            createMockClient(expectedPath, expectedParams, "Not Found", HttpStatusCode.NotFound)
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.getMovies(1)
        }
        assertEquals(TheMovieDataBaseError.ClientError, exception.error)
    }

    @Test
    fun `getMovies throws service unavailable on IOException`() = runTest {
        val mockEngine = MockEngine { throw IOException("No network") }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.getMovies(1)
        }
        assertEquals(TheMovieDataBaseError.ServiceUnavailable, exception.error)
    }

    // -- Tests for searchMovies --

    @Test
    fun `searchMovies returns valid data`() = runTest {
        val expectedPath = listOf("3", "search", "movie")
        val expectedParams = mapOf(
            "include_adult" to "false",
            "language" to "en-US",
            "page" to "1",
            "query" to "Batman"
        )

        val responseJson = """
            {
              "page": 1,
              "results": [
                { "id": 2 }
              ],
              "total_pages": 1,
              "total_results": 1
            }
        """.trimIndent()

        val client = createMockClient(expectedPath, expectedParams, responseJson)
        val api = TheMovieDataBaseApiImpl(client)

        val result = api.searchMovies("Batman", 1)

        assertEquals(1, result.page)
        assertEquals(1, result.movies.size)
        assertEquals(2, result.movies.first().id)
    }

    @Test
    fun `searchMovies throws client error on 404`() = runTest {
        val expectedPath = listOf("3", "search", "movie")
        val expectedParams = mapOf(
            "include_adult" to "false",
            "language" to "en-US",
            "page" to "1",
            "query" to "Batman"
        )

        val client =
            createMockClient(expectedPath, expectedParams, "Not Found", HttpStatusCode.NotFound)
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.searchMovies("Batman", 1)
        }
        assertEquals(TheMovieDataBaseError.ClientError, exception.error)
    }

    @Test
    fun `searchMovies throws service unavailable on IOException`() = runTest {
        val mockEngine = MockEngine { throw IOException("No network") }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.searchMovies("Batman", 1)
        }
        assertEquals(TheMovieDataBaseError.ServiceUnavailable, exception.error)
    }

    // -- Tests for getMovieDetail --

    @Test
    fun `getMovieDetail returns valid data`() = runTest {
        val movieId = "42"
        val expectedPath = listOf("3", "movie", movieId)
        val expectedParams = mapOf("language" to "en-US")

        val responseJson = """
            {
              "id": 42,
              "original_title": "Original Movie Title",
              "overview": "Detailed movie info"
            }
        """.trimIndent()

        val client = createMockClient(expectedPath, expectedParams, responseJson)
        val api = TheMovieDataBaseApiImpl(client)

        val result = api.getMovieDetail(movieId)

        assertEquals(42, result.id)
        assertEquals("Original Movie Title", result.originalTitle)
        assertEquals("Detailed movie info", result.overview)
    }

    @Test
    fun `getMovieDetail throws client error on 404`() = runTest {
        val movieId = "42"
        val expectedPath = listOf("3", "movie", movieId)
        val expectedParams = mapOf("language" to "en-US")

        val client =
            createMockClient(expectedPath, expectedParams, "Not Found", HttpStatusCode.NotFound)
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.getMovieDetail(movieId)
        }
        assertEquals(TheMovieDataBaseError.ClientError, exception.error)
    }

    @Test
    fun `getMovieDetail throws service unavailable on IOException`() = runTest {
        val mockEngine = MockEngine { throw IOException("No network") }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.getMovieDetail("42")
        }
        assertEquals(TheMovieDataBaseError.ServiceUnavailable, exception.error)
    }
}
