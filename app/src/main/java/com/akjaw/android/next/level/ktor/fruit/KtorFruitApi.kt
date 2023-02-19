package com.akjaw.android.next.level.ktor.fruit

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class FruitSchema(
    val name: String = "",
    val id: Int = -1,
    val nutritions: NutritionsSchema = NutritionsSchema()
)

@Serializable
data class NutritionsSchema(
    val carbohydrates: Float = 0.0f,
    val protein: Float = 0.0f,
    val fat: Float = 0.0f,
    val calories: Float = 0.0f,
    val sugar: Float = 0.0f,
)

interface FruitApi {

    suspend fun getFruits(): List<FruitSchema>
}

class KtorFruitApi : FruitApi {

    private val client = HttpClient(CIO) {
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

    override suspend fun getFruits(): List<FruitSchema> =
        try {
            val response = client.get("https://www.fruityvice.com/api/fruit/all")
            response.body()
        } catch (e: Exception) {
            Log.e("Ktor", e.stackTraceToString())
            emptyList()
        }
}
