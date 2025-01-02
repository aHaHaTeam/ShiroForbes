package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.shiroforbes.config.RouterConfig
import java.io.File

fun Routing.externalUrlRoutes(
    routerConfig: RouterConfig,
) {
    get("/favicon.ico") {
        call.respondFile(File("src/main/resources/static/images/shiro&vlasik.png"))
    }

    get("/grobarium") {
        call.respondRedirect(routerConfig.grobariumUrl)
    }

    get("/series") {
        call.respondRedirect(routerConfig.seriesUrl)
    }

    get("/lz") {
        call.respondRedirect(routerConfig.lzUrl)
    }
}