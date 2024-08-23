package com.papsign.ktor.openapigen.route.response

import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.route.OpenAPIRoute
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext

interface Responder {
    suspend fun <TResponse : Any> respond(response: TResponse, request: PipelineContext<Unit, ApplicationCall>)
    suspend fun <TResponse : Any> respond(statusCode: HttpStatusCode,
                                          response: TResponse,
                                          request: PipelineContext<Unit, ApplicationCall>)
}

interface OpenAPIPipelineContext {
    val route: OpenAPIRoute<*>
    val pipeline: PipelineContext<Unit, ApplicationCall>
    val responder: Responder
}

interface OpenAPIPipelineResponseContext<TResponse> : OpenAPIPipelineContext
interface OpenAPIPipelineAuthContext<TAuth, TResponse> : OpenAPIPipelineResponseContext<TResponse> {
    val authProvider: AuthProvider<TAuth>
}

class ResponseContextImpl<TResponse>(
    override val pipeline: PipelineContext<Unit, ApplicationCall>,
    override val route: OpenAPIRoute<*>,
    override val responder: Responder
) : OpenAPIPipelineResponseContext<TResponse>

class AuthResponseContextImpl<TAuth, TResponse>(
    override val pipeline: PipelineContext<Unit, ApplicationCall>,
    override val authProvider: AuthProvider<TAuth>,
    override val route: OpenAPIRoute<*>,
    override val responder: Responder
) : OpenAPIPipelineAuthContext<TAuth, TResponse>


suspend inline fun <reified TResponse : Any> OpenAPIPipelineResponseContext<TResponse>.respond(response: TResponse,
                                                                                               statusCode: HttpStatusCode = HttpStatusCode.OK) {
    responder.respond(statusCode, response as Any, pipeline)
}

suspend inline fun <reified TResponse : Any> OpenAPIPipelineResponseContext<TResponse>.respondWithStatus(
    statusCode: HttpStatusCode
) {
    responder.respond(statusCode, Unit, pipeline)
}
