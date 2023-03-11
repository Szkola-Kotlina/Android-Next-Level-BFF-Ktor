package com.akjaw.plugins

import com.akjaw.android.next.level.bff.shared.authentication.VALID_USERS
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer

val favoriteAuthentication = "favoriteAuthentication"
fun Application.configureSecurity() {
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
}
