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
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
            // https://stackoverflow.com/questions/4779963/how-can-i-access-my-localhost-from-my-android-device
            // adb reverse tcp:8080 tcp:8080
            val response = client.get("http://0.0.0.0:8080/fruits")
            response.body()
        } catch (e: Exception) {
            Log.e("Ktor", e.stackTraceToString())
            emptyList()
        }
}
