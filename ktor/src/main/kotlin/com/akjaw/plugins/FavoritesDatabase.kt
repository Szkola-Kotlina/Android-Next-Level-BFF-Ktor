package com.akjaw.plugins

import com.akjaw.favoriteAuthentication
import com.akjaw.data.createDatabase
import com.akjaw.fruit.FavoriteFruitDao
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun Application.configureFavoritesDatabases() {
    val dao = FavoriteFruitDao(createDatabase())
    routing {
        authenticate(favoriteAuthentication) {
            get<FruitsFavorites> {
                val favorites = dao.getAllFavoriteIds()
                call.respond(favorites)
            }
            post<FruitsFavorites> {
                val id = call.getFruitId() ?: return@post
                val result = dao.addFavorite(id)
                if (result == null) {
                    call.respond(HttpStatusCode.BadRequest, "Fruit with id: $id already favorited")
                } else {
                    call.respond(HttpStatusCode.OK)
                }
            }
            delete<FruitsFavorites> {
                val id = call.getFruitId() ?: return@delete
                val wasDeleted = dao.removeFavorite(id)
                if (wasDeleted) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Fruit with id: $id does not exist")
                }
            }
        }
    }
}

private suspend fun ApplicationCall.getFruitId(): Int? {
    return this.parameters["id"]?.toInt().also { id ->
        if (id == null) {
            this.respond(HttpStatusCode.BadRequest, "id is not valid")
        }
    }
}

@Serializable
@Resource("/fruits/favorites")
class FruitsFavorites
