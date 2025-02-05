package ru.shiroforbes.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import ru.shiroforbes.config.AuthConfig
import ru.shiroforbes.model.Rights
import ru.shiroforbes.service.AdminService
import ru.shiroforbes.service.UserService

fun Application.setupAuth(config: AuthConfig, userService: UserService, adminService: AdminService) {
    install(Sessions) {
        cookie<Session>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 24 * 30
        }
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor app"
            verifier(
                JWT.require(Algorithm.HMAC256(config.secretKey))
                    .withIssuer("ktor-server")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("login").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }

        session<Session>("auth-session") {
            validate { session ->
                if (userRights(userService, session.login, session.password) != null) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }

        session<Session>("auth-session-no-redirect") {
            skipWhen { call -> call.sessions.get<Session>() == null }
            validate { session ->
                if (validUser(userService, session.login, session.password)) {
                    session
                } else {
                    Session("", "")
                }
            }
        }

        session<Session>("auth-session-admin-only") {
            validate { session ->
                if (validAdmin(adminService, session.login, session.password)) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }

        session<Session>("auth-session-at-least-teacher") {
            validate { session ->
                val rights = userRights(userService, session.login, session.password) ?: return@validate null
                if (rights.power >= Rights.Teacher.power) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }
}