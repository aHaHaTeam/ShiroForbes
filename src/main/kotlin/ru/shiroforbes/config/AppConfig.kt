@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import org.jetbrains.exposed.sql.Database
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import ru.shiroforbes.Config
import ru.shiroforbes.login.*
import ru.shiroforbes.model.Rights
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.*
import ru.shiroforbes.web.routes

fun Application.configureApp(config: Config) {
    val database = Database.connect(
        config.dbConfig.connectionUrl,
        config.dbConfig.driver,
        config.dbConfig.user,
        config.dbConfig.password,
    )

    val ratingService: RatingService = DbRatingService(database)
    val studentService: StudentService = DbStudentService(database)
    val adminService: AdminService = DbAdminService(database)
    val userService: UserService = DbUserService(database, studentService)

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

    install(Routing) {
        routes(
            ratingService = ratingService,
            studentService = studentService,
            userService = userService,
            ratingLoaderService = RatingLoaderService(config.googleSheetsConfig),
            routerConfig = config.routerConfig,
        )
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
}
