@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.web

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.RatingService
import ru.shiroforbes.service.StudentService
import ru.shiroforbes.service.UserService
import java.io.File

fun Routing.routes(
    ratingService: RatingService,
    studentService: StudentService,
    userService: UserService,
    ratingLoaderService: RatingLoaderService,
    routerConfig: RouterConfig,
) {
    staticFiles("/static", File("src/main/resources/static/"))

    authenticationRoutes(studentService, userService)
    externalUrlRoutes(routerConfig)
    menuRoutes(userService)
    profileRoutes(userService, studentService, ratingService)
    ratingRoutes(ratingService, studentService, userService, ratingLoaderService)
    testRoutes()
}
