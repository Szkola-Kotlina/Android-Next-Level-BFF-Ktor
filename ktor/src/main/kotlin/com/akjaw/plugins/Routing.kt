package com.akjaw.plugins

import com.akjaw.android.next.level.bff.shared.model.FruitSchema
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.resources.Resources
import kotlinx.serialization.Serializable
import io.ktor.server.application.*
import kotlinx.serialization.json.Json

class OfficialFruitApi {

    private val client = HttpClient(CIO) {
        expectSuccess = true
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun getFruits(): List<FruitSchema> =
        try {
            val response = client.get("https://www.fruityvice.com/api/fruit/all")
            response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
}

fun Application.configureRouting() {
    val officialApi = OfficialFruitApi()
    install(Resources)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get<Fruits> {
            val fruits: List<FruitSchema> = officialApi.getFruits()
            call.respond(fruits)
        }
    }
}

@Serializable
@Resource("/fruits")
class Fruits