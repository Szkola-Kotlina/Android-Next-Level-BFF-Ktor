package com.akjaw.android.next.level.bff.android.fruit

import android.util.Log
import com.akjaw.android.next.level.bff.shared.authentication.VALID_USERS
import com.akjaw.android.next.level.bff.shared.model.FruitSchema
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

interface FruitApi {

    suspend fun getFruits(): List<FruitSchema>

    suspend fun getFavorites(): List<Int>

    suspend fun addFavorite(fruitId: Int): Boolean

    suspend fun removeFavorite(fruitId: Int): Boolean
}

fun createFruitApi(): FruitApi = createOfficialFruitApi()

private fun createInternalFruitApi(): FruitApi {
    val client: HttpClient = createClient()
    val baseUrl = "http://0.0.0.0:8080"
    return KtorFruitApi(
        getFruitsApi = { client.get("$baseUrl/fruits").body() },
        getFavoritesApi = { client.get("$baseUrl/fruits/favorites").body() },
        addFavoriteApi = { fruitId ->
            val response = client.post("$baseUrl/fruits/favorites") {
                url { parameters.append("id", "$fruitId") }
            }
            response.status == HttpStatusCode.OK
        },
        removeFavoriteApi = { fruitId ->
            val response = client.delete("$baseUrl/fruits/favorites") {
                url { parameters.append("id", "$fruitId") }
            }
            response.status == HttpStatusCode.OK
        }
    )
}

private fun createOfficialFruitApi(): FruitApi {
    val client: HttpClient = createClient()
    return KtorFruitApi(
        getFruitsApi = { client.get("https://www.fruityvice.com/api/fruit/all").body() },
        getFavoritesApi = { emptyList() },
        addFavoriteApi = { true },
        removeFavoriteApi = { true },
    )
}

private class KtorFruitApi(
    private val getFruitsApi: suspend () -> List<FruitSchema>,
    private val getFavoritesApi: suspend () -> List<Int>,
    private val addFavoriteApi: suspend (fruitId: Int) -> Boolean,
    private val removeFavoriteApi: suspend (fruitId: Int) -> Boolean,
) : FruitApi {

    override suspend fun getFruits(): List<FruitSchema> =
        try {
            getFruitsApi()
        } catch (e: Exception) {
            Log.e("Ktor", e.stackTraceToString())
            emptyList()
        }

    override suspend fun getFavorites(): List<Int> =
        try {
            getFavoritesApi()
        } catch (e: Exception) {
            Log.e("Ktor", e.stackTraceToString())
            emptyList()
        }

    override suspend fun addFavorite(fruitId: Int): Boolean =
        try {
            addFavoriteApi(fruitId)
        } catch (e: Exception) {
            Log.e("Ktor", e.stackTraceToString())
            false
        }

    override suspend fun removeFavorite(fruitId: Int): Boolean =
        try {
            removeFavoriteApi(fruitId)
        } catch (e: Exception) {
            Log.e("Ktor", e.stackTraceToString())
            false
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
    defaultRequest {
        headers {
            header("Authorization", "Bearer ${VALID_USERS[0]}")
        }
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