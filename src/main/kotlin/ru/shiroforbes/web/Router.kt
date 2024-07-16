@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import ru.shiroforbes.service.GroupService
import java.io.File

fun Routing.rating(groupService: GroupService) {
    staticFiles("/static", File("src/main/resources/static/"))

    get("/rating") {
        call.respond(
            ThymeleafContent(
                "rating",
                mapOf("students" to groupService.getGroup(0).students),
            ),
        )
    }
}
