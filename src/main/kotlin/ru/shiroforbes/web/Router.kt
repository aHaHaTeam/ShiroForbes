@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.model.Admin
import ru.shiroforbes.model.Event
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.GoogleSheetsService
import ru.shiroforbes.modules.googlesheets.RatingRow
import ru.shiroforbes.modules.serialization.RatingSerializer
import ru.shiroforbes.service.EventService
import ru.shiroforbes.service.GroupService
import ru.shiroforbes.service.StudentService
import java.io.ByteArrayOutputStream
import java.io.File

fun Routing.routes(
    groupService: GroupService? = null,
    studentService: StudentService? = null,
    eventService: EventService? = null,
    ratingSerializer: RatingSerializer,
    ratingDeserializer: GoogleSheetsService<RatingRow>,
    routerConfig: RouterConfig? = null,
) {
    staticFiles("/static", File("src/main/resources/static/"))
    get("/favicon.ico") {
        call.respondFile(File("src/main/resources/static/images/shiro&vlasik.png"))
    }

    get("/grobarium") {
        call.respondRedirect(routerConfig!!.grobariumUrl)
    }

    get("/menu") {
        call.respond(
            ThymeleafContent(
                "menu",
                mapOf(
                    "countrysideCampStudents" to groupService!!.getAllGroups()[0].students,
                    "urbanCampStudents" to groupService.getAllGroups()[1].students,
                    "user" to students[1], // TODO() call for proper user
                ),
            ),
        )
    }

    get("/rating") {
        call.respond(
            ThymeleafContent(
                "rating",
                mapOf("students" to groupService!!.getAllGroups()[0].students),
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
                mapOf("students" to groupService!!.getAllGroups()[0].students),
            ),
        )
    }

    get("/profile/{login}") {
        val user = studentService!!.getStudentByLogin(call.parameters["login"]!!)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest)
        }
        call.respond(
            ThymeleafContent(
                "profile",
                mapOf(
                    "user" to user!!,
                    "rating" to user.ratingHistory,
                    "wealth" to user.wealthHistory,
                ),
            ),
        )
    }

    get("/admin") {
        call.respond(
            ThymeleafContent(
                "rating",
                mapOf("students" to groupService!!.getAllGroups()[0].students),
            ),
        )
    }

    get("/download/urban/rating.pdf") {
        val outputStream = ByteArrayOutputStream()
        ratingSerializer.serialize(outputStream, groups[GroupType.UrbanCamp.ordinal].students)
        call.respondBytes(outputStream.toByteArray())
    }

    get("/download/countryside/rating.pdf") {
        val outputStream = ByteArrayOutputStream()
        ratingSerializer.serialize(outputStream, groups[GroupType.CountrysideCamp.ordinal].students)
        call.respondBytes(outputStream.toByteArray())
    }

    // Mock routes for testing
    get("/mock") {
        call.respondRedirect("/menu")
    }

    get("/mock/menu") {
        val groups = MockGroupService.getAllGroups()
        val a = (Admin(1, "vasya", "vasya566", "pass").equals(0))
        call.respond(
            ThymeleafContent(
                "menu",
                mapOf(
                    "countrysideCampStudents" to groups[GroupType.CountrysideCamp.ordinal].students,
                    "urbanCampStudents" to groups[GroupType.UrbanCamp.ordinal].students,
                    "user" to Admin(1, "vasya", "vasya566", "pass"), // TODO() call for proper user
                ),
            ),
        )
    }

    get("/mock/login") {
        call.respondText("login", ContentType.Text.Html)
    }

    get("/mock/profile/{login}") {
        call.respond(
            ThymeleafContent(
                "profile",
                mapOf(
                    "user" to MockStudentService.getStudentByLogin(call.parameters["login"]!!),
                    "rating" to listOf(8, 2, 3, 7, 5, 4),
                    "wealth" to listOf(1, 2, 3, 7, 5, 4, 1, 2, 3, 7, 5, 4, 1, 2, 3, 7, 5, 4, 1, 2),
                ),
            ),
        )
    }

    get("/mock/admin") {
        call.respond(
            ThymeleafContent(
                "admin",
                mapOf("students" to groupService!!.getAllGroups()[0].students),
            ),
        )
    }

    get("/") {
        call.respondRedirect("/mock/menu/")
    }

    get("/event") {
        call.respond(
            ThymeleafContent(
                "eventWorkshop",
                mapOf(
                    "user" to 0, // TODO() call for proper user
                ),
            ),
        )
    }

    post("/event/new") {
        val params = call.receiveParameters()

        val event =
            Event(
                -1,
                params["eventName"].toString(),
                params["timeAndPlace"].toString(),
                params["eventDescription"].toString(),
            )

        eventService!!.addEvent(event)
        call.respond(HttpStatusCode.Accepted)
    }

    post("/profile/investing/{id}") {
        val formContent = call.receiveText()
        val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()["isInvesting"].toString()

        if (params == "true") {
            studentService?.updateStudentInvesting(call.parameters["id"]!!.toInt(), true)
        }
        if (params == "false") {
            studentService?.updateStudentInvesting(call.parameters["id"]!!.toInt(), false)
        }

        call.respond(HttpStatusCode.NoContent)
    }
}
