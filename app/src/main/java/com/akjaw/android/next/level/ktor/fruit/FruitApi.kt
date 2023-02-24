package com.akjaw.android.next.level.ktor.fruit

import android.util.Log
import com.akjaw.android.next.level.ktor.shared.model.FruitSchema
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

interface FruitApi {

    suspend fun getFruits(): List<FruitSchema>
}

fun createFruitApi(): FruitApi = createInternalFruitApi()

fun createInternalFruitApi(): FruitApi {
    val client: HttpClient = createClient()
    return KtorFruitApi(
        getFruitsFromApi = { client.get("http://0.0.0.0:8080/fruits") }
    )
}

fun createOfficialFruitApi(): FruitApi {
    val client: HttpClient = createClient()
    return KtorFruitApi(
        getFruitsFromApi = { client.get("https://www.fruityvice.com/api/fruit/all") }
    )
}

private class KtorFruitApi(
    private val getFruitsFromApi: suspend () -> HttpResponse,
) : FruitApi {

    override suspend fun getFruits(): List<FruitSchema> =
        try {
            val response = getFruitsFromApi()
            response.body()
        } catch (e: Exception) {
            Log.e("Ktor", e.stackTraceToString())
            emptyList()
        }
}

private fun createClient() = HttpClient(CIO) {
    expectSuccess = true
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.d("Ktor", message)
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