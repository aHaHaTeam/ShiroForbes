@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.Authentication.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import ru.shiroforbes.login.Session
import ru.shiroforbes.modules.serialization.RatingSerializer
import ru.shiroforbes.service.DbEventService
import ru.shiroforbes.service.DbStudentService
import ru.shiroforbes.web.routes

fun Application.configureApp() {
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
            cookie.maxAgeInSeconds = 60
        }
    }

    install(Authentication) {
        form("auth-form") {
            userParamName = "login"
            passwordParamName = "password"
            validate { credentials ->
                if (ru.shiroforbes.login.validate(credentials.name, credentials.password)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
        session<Session>("auth-session") {
            validate { session ->
                session
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

    install(Routing) {
        routes(
            studentService = DbStudentService,
            ratingSerializer = RatingSerializer(),
            eventService = DbEventService,
        )
    }
}
