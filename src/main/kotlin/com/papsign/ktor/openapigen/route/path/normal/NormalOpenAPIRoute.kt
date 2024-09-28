package com.papsign.ktor.openapigen.route.path.normal

import com.papsign.ktor.openapigen.modules.CachingModuleProvider
import com.papsign.ktor.openapigen.route.OpenAPIRoute
import com.papsign.ktor.openapigen.route.VirtualWebHandlerExecutor
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import com.papsign.ktor.openapigen.route.response.ResponseContextImpl
import io.ktor.server.routing.Route
import kotlinx.coroutines.withContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class NormalOpenAPIRoute(route: Route, provider: CachingModuleProvider = CachingModuleProvider()) :
    OpenAPIRoute<NormalOpenAPIRoute>(route, provider) {

    override fun child(route: Route): NormalOpenAPIRoute {
        return NormalOpenAPIRoute(route, provider.child())
    }

    @PublishedApi
    internal fun <P : Any, R : Any, B : Any> handle(
        pType: KType,
        rType: KType,
        bType: KType,
        body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
    ) {
        handle<P, R, B>(pType, rType, bType) { pipeline, responder, p, b ->
            withContext(VirtualWebHandlerExecutor.executor) {
                ResponseContextImpl<R>(pipeline, this@handle, responder).body(p, b)
            }
        }
    }

    @PublishedApi
    internal fun <P : Any, R : Any> handle(
        pType: KType,
        rType: KType,
        body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
    ) {
        handle<P, R, Unit>(pType, rType, typeOf<Unit>()) { pipeline, responder, p, _ ->
            withContext(VirtualWebHandlerExecutor.executor) {
                ResponseContextImpl<R>(pipeline, this@handle, responder).body(p)
            }
        }
    }
}
