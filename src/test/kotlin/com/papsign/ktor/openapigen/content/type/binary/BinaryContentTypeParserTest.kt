package com.papsign.ktor.openapigen.content.type.binary

import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.response.respondWithStatus
import com.papsign.ktor.openapigen.route.route
import installOpenAPI
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.InputStream
import kotlin.random.Random

const val contentType = "image/png"

class BinaryContentTypeParserTest {


    @BinaryRequest([contentType])
    @BinaryResponse([contentType])
    data class Stream(val stream: InputStream)

    @Test
    fun testBinaryParsing() {
        val route = "/test"
        val bytes = Random.nextBytes(20)
        withTestApplication({
            installOpenAPI()
            apiRouting {
                //(this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<Unit, Stream, Stream> { _, body ->
                        val actual = body.stream.readBytes()
                        Assertions.assertArrayEquals(bytes, actual)
                        respond(Stream(actual.inputStream()))
                    }
                }
                route("forbidden") {
                    post<Unit, Stream, Stream> { _, body ->
                        respondWithStatus(HttpStatusCode.Forbidden)
                    }
                }
            }
        }) {

            println("Test: Normal")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, contentType)
                addHeader(HttpHeaders.Accept, contentType)
                setBody(bytes)
            }.apply {
                Assertions.assertEquals(ContentType.parse(contentType), response.contentType())
                Assertions.assertArrayEquals(bytes, response.byteContent)
            }

            println("Test: Missing Accept")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, contentType)
                setBody(bytes)
            }.apply {
                Assertions.assertEquals(ContentType.parse(contentType), response.contentType())
                Assertions.assertArrayEquals(bytes, response.byteContent)
            }

            println("Test: Missing Content-Type")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.Accept, contentType)
                setBody(bytes)
            }.apply {
                Assertions.assertEquals(HttpStatusCode.UnsupportedMediaType, response.status())
            }

            println("Test: Bad Accept")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, contentType)
                addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
                setBody(bytes)
            }.apply {
                Assertions.assertEquals(HttpStatusCode.BadRequest, response.status())
            }

            println("Test: Bad Content-Type")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Accept, contentType)
                setBody(bytes)
            }.apply {
                Assertions.assertEquals(HttpStatusCode.UnsupportedMediaType, response.status())
            }

            println("Test: Forbidden, respondWithStatus")
            handleRequest(HttpMethod.Post, "forbidden") {
                addHeader(HttpHeaders.ContentType, contentType)
                addHeader(HttpHeaders.Accept, contentType)
                setBody(bytes)
            }.apply {
                Assertions.assertEquals(HttpStatusCode.Forbidden, response.status())
            }
        }
    }
}
