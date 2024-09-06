package com.papsign.ktor.openapigen

import java.util.*
import kotlin.jvm.java

object SwaggerUIVersion {

    val version: String

    init {
        val file = this::class.java.classLoader.getResourceAsStream("version.properties")
        val properties = Properties()
        properties.load(file)
        version = properties.getProperty("swagger-ui.version")
    }
}
