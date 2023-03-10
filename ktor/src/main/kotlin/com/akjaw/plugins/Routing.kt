package com.akjaw.plugins

import com.akjaw.android.next.level.bff.shared.model.FruitSchema
import com.akjaw.plugins.OfficialFruitApi.Companion.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.resources.Resources
import kotlinx.serialization.Serializable
import io.ktor.server.application.*
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

class OfficialFruitApi(private val client: HttpClient) {

    companion object {

        fun createHttpClient() = HttpClient(CIO) {
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

fun Application.configureRouting(httpClient: HttpClient = createHttpClient()) {
    val officialApi = OfficialFruitApi(httpClient)
    val favoritesDao = FruitsFavoritesDao()
    install(Resources)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get<Fruits> {
            val fruits: List<FruitSchema> = officialApi.getFruits()
            call.respond(fruits)
        }
        get<Fruits.Favorites> {
            call.respond(favoritesDao.getAllFavorites())
        }
        post<Fruits.Favorites> { favorites ->
            val id = favorites.id ?: return@post call.respond(HttpStatusCode.BadRequest, "Id is missing")
            val wasInserted = favoritesDao.insertFavorite(id)
            if (wasInserted != null) {
                call.respondText("Favorite $id added")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Favorite $id already exists")
            }
        }
        delete<Fruits.Favorites> { favorites ->
            val id = favorites.id ?: return@delete call.respond(HttpStatusCode.BadRequest, "Id is missing")
            val wasDeleted = favoritesDao.deleteFavorite(id)
            if (wasDeleted) {
                call.respondText("Favorite with $id deleted")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Favorite with $id does not exist")
            }
        }
    }
}

@Serializable
@Resource("/fruits")
class Fruits {

    @Resource("favorites")
    class Favorites(val parent: Fruits = Fruits(), val id: Int? = null)
}
