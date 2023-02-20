package com.akjaw.plugins

import com.akjaw.fruit.KtorFruitApi
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    val client = KtorFruitApi()
    install(Resources)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get<Fruits> {
            call.respond(client.getFruits())
        }
    }
}

@Serializable
@Resource("/fruits")
class Fruits
