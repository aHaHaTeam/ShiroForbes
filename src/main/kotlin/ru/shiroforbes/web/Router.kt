@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.web

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import java.io.File

fun Routing.routes(
    ratingDeserializer: RatingDeserializer,
    routerConfig: RouterConfig,
) {
    staticFiles("/static", File("src/main/resources/static/"))

    authenticationRoutes()
    externalUrlRoutes(routerConfig)
    menuRoutes()
    profileRoutes(ratingDeserializer)
    ratingRoutes(ratingDeserializer)
}

