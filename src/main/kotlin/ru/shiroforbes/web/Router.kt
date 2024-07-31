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
import ru.shiroforbes.model.GroupType
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
) {
    staticFiles("/static", File("src/main/resources/static/"))
    get("/favicon.ico") {
        call.respondFile(File("src/main/resources/static/images/shiro&vlasik.png"))
    }

    get("/menu") {
        call.respond(
            ThymeleafContent(
                "menu",
                mapOf("students" to groupService!!.getAllGroups()[0].students),
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

//    get("/profile/{id}") {
//        val user = studentService!!.getStudentById(call.parameters["id"]!!.toInt())
//        if (user == null) {
//            call.respond(HttpStatusCode.BadRequest)
//        }
//        call.respond(
//            ThymeleafContent(
//                "profile",
//                mapOf(
//                    "user" to user!!,
//                    "rating" to user.ratingHistory,
//                    "wealth" to user.wealthHistory,
//                ),
//            ),
//        )
//    }

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
        call.respond(
            ThymeleafContent(
                "menu",
                mapOf(
                    "countrysideCampStudents" to groups[GroupType.CountrysideCamp.ordinal].students,
                    "urbanCampStudents" to groups[GroupType.UrbanCamp.ordinal].students,
                    "isLoggedIn" to false,
                    "events" to events,
                ),
            ),
        )
    }

    get("/mock/login") {
        call.respondText("login", ContentType.Text.Html)
    }

//    get("/mock/profile/{id}") {
//        call.respond(
//            ThymeleafContent(
//                "profile",
//                mapOf(
//                    "user" to MockStudentService.getStudentById(call.parameters["id"]!!.toInt()),
//                    "rating" to listOf(8, 2, 3, 7, 5, 4),
//                    "wealth" to listOf(1, 2, 3, 7, 5, 4, 1, 2, 3, 7, 5, 4, 1, 2, 3, 7, 5, 4, 1, 2),
//                ),
//            ),
//        )
//    }

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

    get("/event/{id}") {
        call.respond(
            ThymeleafContent(
                "event",
                mapOf(
                    "isLoggedIn" to false,
                    "event" to MockEventService.getEvent(call.parameters["id"]!!.toInt())!!,
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

    post("/profile/investing/{id}") {
        val formContent = call.receiveText()
        println(formContent)
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
