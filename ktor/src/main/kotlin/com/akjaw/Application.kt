package com.akjaw

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.akjaw.plugins.*
import io.ktor.server.resources.Resources

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Resources)
    configureSerialization()
    configureFavoritesDatabases()
    configureRouting()
}
