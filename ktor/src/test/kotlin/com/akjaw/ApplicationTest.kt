package com.akjaw

import com.akjaw.android.next.level.bff.shared.model.FruitSchema
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import com.akjaw.plugins.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ApplicationTest {
    @Test
    fun e2eToRetrieveFruits() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            configureRouting()
        }
        client.get("/fruits").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyString = bodyAsText()
            val fruits = Json.decodeFromString<List<FruitSchema>>(bodyString)
            assertEquals(40, fruits.count())
        }
    }

    @Test
    fun fruitsEndpointReturnsWhatOfficialApiReturns() = testApplication {
        externalServices {
            hosts("https://www.fruityvice.com") {
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    get("api/fruit/all") {
                        call.respondText(RESPONSE, ContentType.Application.Json)
                    }
                }
            }
        }
        val testHttpClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }
        application {
            install(ContentNegotiation) {
                json()
            }
            configureRouting(testHttpClient)
        }
        client.get("/fruits").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyString = bodyAsText()
            val fruits = Json.decodeFromString<List<FruitSchema>>(bodyString)
            assertEquals(3, fruits.count())
        }
    }

    @Test
    fun fruitsEndpointReturnsEmptyListOnOfficialApiError() = testApplication {
        externalServices {
            hosts("https://www.fruityvice.com") {
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    get("api/fruit/all") {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
        }
        val testHttpClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }
        application {
            install(ContentNegotiation) {
                json()
            }
            configureRouting(testHttpClient)
        }
        client.get("/fruits").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyString = bodyAsText()
            val fruits = Json.decodeFromString<List<FruitSchema>>(bodyString)
            assertEquals(0, fruits.count())
        }
    }

}

val RESPONSE = """
    [
        {
            "name": "Apple",
            "id": 6,
            "nutritions": {
                "carbohydrates": 11.4,
                "protein": 0.3,
                "fat": 0.4,
                "calories": 52.0,
                "sugar": 10.3
            }
        },
        {
            "name": "Apricot",
            "id": 35,
            "nutritions": {
                "carbohydrates": 3.9,
                "protein": 0.5,
                "fat": 0.1,
                "calories": 15.0,
                "sugar": 3.2
            }
        },
        {
            "name": "Avocado",
            "id": 84,
            "nutritions": {
                "carbohydrates": 8.53,
                "protein": 2.0,
                "fat": 14.66,
                "calories": 160.0,
                "sugar": 0.66
            }
        }
    ]
""".trimIndent()