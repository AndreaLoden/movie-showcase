import com.andrea.imdbshowcase.network.utils.Helpers
import com.andrea.imdbshowcase.network.utils.TheMovieDataBaseError
import com.andrea.imdbshowcase.network.utils.TheMovieDataBaseException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HandleErrorsTest {

    private fun createMockClient(
        status: HttpStatusCode,
        responseBody: String? = null
    ): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler { _ ->
                    respond(
                        content = responseBody ?: "",
                        status = status,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
        }
    }

    @Test
    fun `handleErrors returns body on 200 response`() = runTest {
        val client = createMockClient(HttpStatusCode.OK, """{"message":"success"}""")

        val responseLambda: suspend () -> HttpResponse = {
            client.get("http://test")
        }

        val result: String = Helpers.handleErrors(responseLambda)
        assertEquals("""{"message":"success"}""", result)
    }

    @Test
    fun `handleErrors throws ClientError on 400 response`() = runTest {
        val client = createMockClient(HttpStatusCode.BadRequest)

        val responseLambda: suspend () -> HttpResponse = {
            client.get("http://test")
        }

        val exception = assertFailsWith<TheMovieDataBaseException> {
            Helpers.handleErrors<String>(responseLambda)
        }
        assertEquals(TheMovieDataBaseError.ClientError, exception.error)
    }

    @Test
    fun `handleErrors throws ServerError on 500 response`() = runTest {
        val client = createMockClient(HttpStatusCode.InternalServerError)

        val responseLambda: suspend () -> HttpResponse = {
            client.get("http://test")
        }

        val exception = assertFailsWith<TheMovieDataBaseException> {
            Helpers.handleErrors<String>(responseLambda)
        }
        assertEquals(TheMovieDataBaseError.ServerError, exception.error)
    }

    @Test
    fun `handleErrors throws ServiceUnavailable on IOException`() = runTest {
        val responseLambda: suspend () -> HttpResponse = {
            throw IOException("Network error")
        }

        val exception = assertFailsWith<TheMovieDataBaseException> {
            Helpers.handleErrors<String>(responseLambda)
        }
        assertEquals(TheMovieDataBaseError.ServiceUnavailable, exception.error)
    }
}
