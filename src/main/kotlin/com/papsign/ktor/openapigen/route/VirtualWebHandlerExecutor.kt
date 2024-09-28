package com.papsign.ktor.openapigen.route

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Executor som flytter prosessering over til virtuelle tråder for å forlate orginal corutinecontext
 */
object VirtualWebHandlerExecutor {
    val executor = Executors.newFixedThreadPool(
        10, Thread.ofVirtual()
            .name("web-route-", 1L)
            .factory()
    ).asCoroutineDispatcher()

}
