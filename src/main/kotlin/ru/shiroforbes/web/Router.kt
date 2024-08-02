@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.login.Session
import ru.shiroforbes.login.isAdmin
import ru.shiroforbes.login.validUser
import ru.shiroforbes.model.Admin
import ru.shiroforbes.model.Event
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.GoogleSheetsService
import ru.shiroforbes.modules.googlesheets.RatingRow
import ru.shiroforbes.modules.serialization.RatingSerializer
import ru.shiroforbes.service.EventService
import ru.shiroforbes.service.StudentService
import java.io.ByteArrayOutputStream
import java.io.File

fun Routing.routes(
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

    get("/series") {
        call.respondRedirect(routerConfig!!.seriesUrl)
    }

    get("/lz") {
        call.respondRedirect(routerConfig!!.lzUrl)
    }

    get("/menu") {
        call.respond(
            ThymeleafContent(
                "menu",
                mapOf(
                    "countrysideCampStudents" to studentService!!.getGroup(GroupType.Countryside),
                    "urbanCampStudents" to studentService.getGroup(GroupType.Urban),
                    "user" to students[1], // TODO() call for proper user
                ),
            ),
        )
    }

    get("/rating") {
        call.respond(
            ThymeleafContent(
                "rating",
                mapOf("students" to studentService!!.getGroup(GroupType.Countryside)),
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
                mapOf("students" to studentService!!.getGroup(GroupType.Countryside)),
            ),
        )
    }

    post("/login") {
        val formContent = call.receiveText()
        val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()
        val name = params.jsonValue("login")
        val password = params.jsonValue("password")
        if (!validUser(name, password)) {
            return@post
        }
        call.sessions.set(Session(name, password))
        if (isAdmin(name)) {
            call.respondRedirect("/admin")
            return@post
        }
        val user = studentService!!.getStudentByLogin(name)
        call.respondRedirect("/profile/${user!!.id}")
    }

    authenticate("auth-session") {
        get("/profile/{id}") {
            val user = studentService!!.getStudentById(call.parameters["id"]!!.toInt())
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else if (user.login != call.principal<Session>()!!.login) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                call.respond(
                    ThymeleafContent(
                        "profile",
                        mapOf(
                            "user" to user,
                            "rating" to listOf(8, 2, 3, 7, 5, 4),
                            "wealth" to listOf(1, 2, 3, 7, 5, 4),
                        ),
                    ),
                )
            }
        }
    }

    authenticate("auth-session") {
        get("/admin") {
            if (!isAdmin(call.principal<Session>()?.login)) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                call.respond(
                    ThymeleafContent(
                        "components/rating",
                        mapOf("students" to studentService!!.getGroup(GroupType.Countryside)),
                    ),
                )
            }
        }
    }

    authenticate("auth-session") {
        get("/mock/profile/{id}") {
            call.respond(
                ThymeleafContent(
                    "profile",
                    mapOf(
                        "user" to MockStudentService.getStudentById(call.parameters["id"]!!.toInt()),
                        "rating" to listOf(8, 2, 3, 7, 5, 4),
                        "wealth" to listOf(1, 2, 3, 7, 5, 4, 1, 2, 3, 7, 5, 4, 1, 2, 3, 7, 5, 4, 1, 2),
                    ),
                ),
            )
        }
    }

    authenticate("auth-session") {
        get("/mock/admin") {
            call.respond(
                ThymeleafContent(
                    "admin",
                    mapOf("students" to studentService!!.getGroup(GroupType.Countryside)),
                ),
            )
        }
    }

    authenticate("auth-session") {
        post("/profile/investing/{id}") {
            val user = studentService!!.getStudentById(call.parameters["id"]!!.toInt())
            if (user!!.login != call.principal<Session>()!!.login) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val formContent = call.receiveText()
            println(formContent)
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()["isInvesting"].toString()

            if (params == "true") {
                studentService.updateStudentInvesting(call.parameters["id"]!!.toInt(), true)
            }
            if (params == "false") {
                studentService.updateStudentInvesting(call.parameters["id"]!!.toInt(), false)
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }

    get("/download/urban/rating.pdf") {
        val outputStream = ByteArrayOutputStream()
        ratingSerializer.serialize(outputStream, studentService!!.getGroup(GroupType.Urban))
        call.respondBytes(outputStream.toByteArray())
    }

    get("/download/countryside/rating.pdf") {
        val outputStream = ByteArrayOutputStream()
        ratingSerializer.serialize(outputStream, studentService!!.getGroup(GroupType.Countryside))
        call.respondBytes(outputStream.toByteArray())
    }

    // Mock routes for testing
    get("/mock") {
        call.respondRedirect("/menu")
    }

    get("/transactions/exercises/countryside") {
        val countryside = studentService!!.getGroup(GroupType.Countryside)
        val a = (Admin(1, "vasya", "vasya566", "pass", GroupType.Countryside).equals(0))
        call.respond(
            ThymeleafContent(
                "exercises",
                mapOf(
                    "students" to countryside,
                    "user" to
                        Admin(
                            1,
                            "vasya",
                            "vasya566",
                            "pass",
                            GroupType.Countryside,
                        ),
                    // TODO() call for proper user
                ),
            ),
        )
    }

    get("/mock/menu") {
        val countryside = studentService!!.getGroup(GroupType.Countryside)
        val urban = studentService.getGroup(GroupType.Urban)
        val a = (Admin(1, "vasya", "vasya566", "pass", GroupType.Countryside).equals(0))
        call.respond(
            ThymeleafContent(
                "menu",
                mapOf(
                    "countrysideCampStudents" to countryside,
                    "urbanCampStudents" to urban,
                    "countrysideCampEvents" to listOf<Event>(),
                    "urbanCampEvents" to listOf<Event>(),
                    "user" to
                        Admin(
                            1,
                            "vasya",
                            "vasya566",
                            "pass",
                            GroupType.Countryside,
                        ),
                    // TODO() call for proper user
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

    get("/") {
        call.respondRedirect("/mock/menu")
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

    post("/profile/investing") {
        val formContent = call.receiveText()
        val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()

        val id = params["userId"].toString().toInt()
        val investing = params["isInvesting"].toString()
        if (investing == "true") {
            studentService?.updateStudentInvesting(id, true)
        }
        if (investing == "false") {
            studentService?.updateStudentInvesting(id, false)
        }

        call.respond(HttpStatusCode.NoContent)
    }
}

private fun Map<String, JsonElement>.jsonValue(key: String): String {
    val quoted = this[key].toString()
    return quoted.substring(1, quoted.length - 1)

}


