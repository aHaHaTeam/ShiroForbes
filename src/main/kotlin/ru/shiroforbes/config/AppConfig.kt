@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.config

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.jetbrains.exposed.sql.Database
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import ru.shiroforbes.Config
import ru.shiroforbes.auth.setupAuth
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.*
import ru.shiroforbes.web.routes

fun Application.configureApp(config: Config) {
    val database =
        Database.connect(
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

    setupAuth(config.authConfig, userService, adminService)

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
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
        allowHost("localhost:5173")
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowCredentials = true
    }

    install(ContentNegotiation) {
        json()
    }
}
