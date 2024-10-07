import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MinimalExampleTest {
    @Test
    fun `test that minimal example works`(): Unit = testApplication {
        application(Application::minimalExample)

        val client = createClient { install(ContentNegotiation) { jackson() } }

        val openapi = client.get("/openapi.json")
        val body = openapi.body<String>()
        assertEquals(HttpStatusCode.OK, openapi.status)
        assertTrue(body.isNotEmpty())

        val response = client.post("/example/world") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(SomeRequest(foo = "hello from body"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(SomeResponse(bar = "Hello world! From body: hello from body."), response.body())
    }

    @Test
    fun `test thadddt minimal example works`(): Unit = testApplication {
        application(Application::minimalExample)

        val client = createClient { install(ContentNegotiation) { jackson() } }

        val response = client.get("/forbidden/112") {
            header(HttpHeaders.ContentType, ContentType.Application.Pdf.toString())
        }
        assertEquals(HttpStatusCode.Forbidden,  response.status)
        assertEquals("{}", response.body<String>())
    }
}
