@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import ru.shiroforbes.service.GroupService
import java.io.File

fun Routing.routes(groupService: GroupService?) {
    staticFiles("/static", File("src/main/resources/static/"))

    get("/menu") {
        call.respond(
            ThymeleafContent(
                "menu",
                mapOf("students" to groupService!!.getGroup(0).students),
            ),
        )
    }

    get("/rating") {
        call.respond(
            ThymeleafContent(
                "rating",
                mapOf("students" to groupService!!.getGroup(0).students),
            ),
        )
    }

    get("/login") {
        call.respond(
            ThymeleafContent(
                "login",
                mapOf(),
            ),
        )
    }

    get("/profile") {
        call.respond(
            ThymeleafContent(
                "rating",
                mapOf("students" to groupService!!.getGroup(0).students),
            ),
        )
    }

    get("/admin") {
        call.respond(
            ThymeleafContent(
                "rating",
                mapOf("students" to groupService!!.getGroup(0).students),
            ),
        )
    }

    // Mock routes for testing
    get("/mock") {
        call.respondRedirect("/menu")
    }

    get("/mock/menu") {
        call.respond(
            ThymeleafContent(
                "menu",
                mapOf(
                    "user" to students[1],
                    "isLoggedIn" to true,
                    "groups" to MockGroupService.getAllGroups(),
                ),
            ),
        )
    }

    get("/mock/rating") {
        call.respond(
            ThymeleafContent(
                "components/rating",
                mapOf("groups" to MockGroupService.getAllGroups()),
            ),
        )
    }

    get("/mock/login") {
        call.respondText("login", ContentType.Text.Html)
    }

    get("/mock/profile/{id}") {
        call.respond(
            ThymeleafContent(
                "profile",
                mapOf(
                    "user" to MockStudentService.getStudent(call.parameters["id"]!!.toInt()),
                    "rating" to listOf(8, 2, 3, 7, 5, 4),
                    "wealth" to listOf(1, 2, 3, 7, 5, 4),
                ),
            ),
        )
    }

    get("/mock/admin") {
        call.respond(
            ThymeleafContent(
                "admin",
                mapOf("students" to groupService!!.getGroup(0).students),
            ),
        )
    }

    get("/") {
        call.respondRedirect("/mock/profile/1")
    }
}
