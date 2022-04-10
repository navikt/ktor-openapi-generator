package com.papsign.ktor.openapigen.content.type

import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KType

interface BodyParser: ContentTypeProvider {
    fun <T: Any> getParseableContentTypes(type: KType): List<ContentType>
    suspend fun <T: Any> parseBody(clazz: KType, request: PipelineContext<Unit, ApplicationCall>): T
}
