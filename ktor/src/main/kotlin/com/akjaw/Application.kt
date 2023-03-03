package com.akjaw

import com.akjaw.android.next.level.ktor.shared.authentication.VALID_USERS
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.akjaw.plugins.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

val favoriteAuthentication = "favoriteAuthentication"

fun Application.module() {
    install(Resources)
    install(ContentNegotiation) {
        json()
    }
    install(Authentication) {
        bearer(favoriteAuthentication) {
            realm = "Access to the '/fruits/favorites' path"
            authenticate { tokenCredential ->
                if (VALID_USERS.contains(tokenCredential.token)) {
                    UserIdPrincipal(tokenCredential.token)
                } else {
                    null
                }
            }
        }
    }
    install(CallLogging)
    configureFavoritesDatabases()
    configureRouting()
}
