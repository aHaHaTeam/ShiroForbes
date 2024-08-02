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
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.login.Session
import ru.shiroforbes.login.isAdmin
import ru.shiroforbes.model.Event
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

    authenticate("auth-form") {
        post("/login") {
            val name = call.principal<UserIdPrincipal>()!!.name
            call.sessions.set(Session(name))
            if (name == "admin") {
                call.respondRedirect("/admin")
                return@post
            }
            val user = studentService!!.getStudentByLogin(name)
            call.respondRedirect("/profile/${user!!.id}")
        }
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
                        "menu",
                        mapOf("events" to listOf<Event>()),
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
                    mapOf("students" to groupService!!.getAllGroups()[0].students),
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

    get("/mock/event") {
        call.respond(ThymeleafContent("eventWorkshop", mapOf("isLoggedIn" to false)))
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
        call.respond(HttpStatusCode.NoContent)
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
