package com.papsign.ktor.openapigen

import java.util.Properties

object SwaggerUIVersion {

    private val log = classLogger()

    val version: String

    init {
        // First try to read from our generated properties file
        val versionPropertiesFile = this::class.java.getResourceAsStream("/version.properties")
        if (versionPropertiesFile != null) {
            val versionProperties = Properties()
            versionProperties.load(versionPropertiesFile)
            version = versionProperties.getProperty("swagger.ui.version")
            log.debug("Using Swagger UI version from version.properties: $version")
        } else {
            // Fall back to reading from Maven properties
            val mavenPropertiesFile = this::class.java.getResourceAsStream("/META-INF/maven/org.webjars/swagger-ui/pom.properties")
            if (mavenPropertiesFile != null) {
                val mavenProperties = Properties()
                mavenProperties.load(mavenPropertiesFile)
                version = mavenProperties.getProperty("version")
                log.debug("Using Swagger UI version from Maven properties: $version")
            } else {
                log.warn("Could not get Swagger UI version from properties files")
                // This fallback should never be used in normal operation
                version = "5.20.7"
            }
        }
    }
}
