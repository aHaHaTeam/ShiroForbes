@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.config

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import ru.shiroforbes.Config
import ru.shiroforbes.login.Session
import ru.shiroforbes.login.knownPasswords
import ru.shiroforbes.login.validAdmin
import ru.shiroforbes.login.validUser
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import ru.shiroforbes.web.routes

fun Application.configureApp(config: Config) {
    install(Thymeleaf) {
        setTemplateResolver(
            ClassLoaderTemplateResolver().apply {
                prefix = "templates/thymeleaf/"
                suffix = ".html"
                characterEncoding = "utf-8"
            },
        )
    }

    install(Sessions) {
        cookie<Session>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 24 * 30
        }
    }

    install(Authentication) {
        form("auth-form") {
            userParamName = "login"
            passwordParamName = "password"
            validate { credentials ->
                UserHashedTableAuth(
                    { it.toByteArray() },
                    knownPasswords(),
                ).authenticate(credentials)
            }
        }
        session<Session>("auth-session") {
            validate { session ->
                if (validUser(session.login, session.password)) {
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
                if (validUser(session.login, session.password)) {
                    session
                } else {
                    Session("", "")
                }
            }
        }

        session<Session>("auth-session-admin-only") {
            validate { session ->
                if (validAdmin(session.login, session.password)) {
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

    install(Routing) {
        routes(
            // studentService = DbStudentService,
            ratingDeserializer = RatingDeserializer(config.googleSheetsConfig),
            routerConfig = config.routerConfig,
        )
    }
}
