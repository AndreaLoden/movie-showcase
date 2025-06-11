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
        responseJson: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ): HttpClient {
        val mockEngine = MockEngine { _ ->
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

    // --- Tests for getMovies ---

    @Test
    fun `getMovies returns valid data`() = runTest {
        val responseJson = """
            {
              "page": 1,
              "results": [
                {
                  "id": 1
                }
              ],
              "total_pages": 1,
              "total_results": 1
            }
        """.trimIndent()

        val client = createMockClient(responseJson)
        val api = TheMovieDataBaseApiImpl(client)

        val result = api.getMovies(1)

        assertEquals(1, result.page)
        assertEquals(1, result.movies.size)
        assertEquals(1, result.movies.first().id)
    }

    @Test
    fun `getMovies throws client error on 404`() = runTest {
        val client = createMockClient("Not Found", HttpStatusCode.NotFound)
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

    @Test
    fun `getMovies throws server error on invalid JSON`() = runTest {
        val client = createMockClient("{ invalid json }", HttpStatusCode.OK)
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.getMovies(1)
        }
        assertEquals(TheMovieDataBaseError.ServerError, exception.error)
    }

    // --- Tests for getMovieDetail ---

    @Test
    fun `getMovieDetail returns valid data`() = runTest {
        val responseJson = """
            {
              "id": "42",
              "title": "The Answer",
              "original_title": "Original Movie Title",
              "overview": "Detailed movie info"
            }
        """.trimIndent()

        val client = createMockClient(responseJson)
        val api = TheMovieDataBaseApiImpl(client)

        val result = api.getMovieDetail("42")

        assertEquals(
            "42",
            result.id.toString()
        ) // Assuming your MovieDetailsDto id is a String or Int
        assertEquals("The Answer", result.title)
    }

    @Test
    fun `getMovieDetail throws client error on 404`() = runTest {
        val client = createMockClient("Not Found", HttpStatusCode.NotFound)
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.getMovieDetail("42")
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

    @Test
    fun `getMovieDetail throws server error on invalid JSON`() = runTest {
        val client = createMockClient("{ invalid json }", HttpStatusCode.OK)
        val api = TheMovieDataBaseApiImpl(client)

        val exception = assertFailsWith<TheMovieDataBaseException> {
            api.getMovieDetail("42")
        }
        assertEquals(TheMovieDataBaseError.ServerError, exception.error)
    }
}
