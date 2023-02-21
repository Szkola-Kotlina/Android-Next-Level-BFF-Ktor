package com.akjaw.plugins

import com.akjaw.data.createDatabase
import com.akjaw.fruit.FavoriteFruitDao
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun Application.configureFavoritesDatabases() {
    val dao = FavoriteFruitDao(createDatabase())
    routing {
        get<FruitsFavorites> {
            val favorites = dao.getAllFavoriteIds()
            call.respond(favorites)
        }
        post<FruitsFavorites> {
            val id = call.getId() ?: return@post
            dao.addFavorite(id)
            call.respond(HttpStatusCode.OK)
        }
        delete<FruitsFavorites> {
            val id = call.getId() ?: return@delete
            dao.removeFavorite(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}

private suspend fun ApplicationCall.getId(): Int? {
    return this.parameters["id"]?.toInt().also { id ->
        if (id == null) {
            this.respond(HttpStatusCode.BadRequest, "id is not valid")
        }
    }
}

@Serializable
@Resource("/fruits/favorites")
class FruitsFavorites
