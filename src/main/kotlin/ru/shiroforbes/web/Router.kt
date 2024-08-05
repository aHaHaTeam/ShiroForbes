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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.login.Session
import ru.shiroforbes.login.isAdmin
import ru.shiroforbes.login.validUser
import ru.shiroforbes.model.*
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import ru.shiroforbes.modules.serialization.RatingSerializer
import ru.shiroforbes.service.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate

fun Routing.routes(
    studentService: StudentService? = null,
    eventService: EventService? = null,
    ratingSerializer: RatingSerializer,
    ratingDeserializer: RatingDeserializer,
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

    authenticate("auth-session-no-redirect") {
        get("/menu") {
            var user: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    user = DbUserService.getUserByLogin(call.principal<Session>()?.login!!) ?: 0
                }
            }

            call.respond(
                ThymeleafContent(
                    "menu",
                    mapOf(
                        "countrysideCampStudents" to
                            studentService!!
                                .getGroup(GroupType.Countryside)
                                .sortedByDescending { it.rating + it.wealth },
                        "urbanCampStudents" to
                            studentService
                                .getGroup(GroupType.Urban)
                                .sortedByDescending { it.rating + it.wealth },
                        "user" to user,
                        "countrysideCampEvents" to listOf<Event>(),
                        "urbanCampEvents" to listOf<Event>(),
                    ),
                ),
            )
        }
    }

    get("/login") {
        call.respond(
            ThymeleafContent(
                "login",
                mapOf(),
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
            call.respondRedirect("/menu")
            return@post
        }
        val user = DbUserService.getUserByLogin(name)
        call.respondRedirect("/profile/${user!!.login}")
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

    authenticate("auth-session-no-redirect") {
        get("/profile/{login}") {
            var activeUser: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    activeUser = DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0
                }
            }
            val user = DbUserService.getUserByLogin(call.parameters["login"]!!)!!
            if (!user.HasAdminRights) {
                user as Student
                call.respond(
                    ThymeleafContent(
                        "profile",
                        mapOf(
                            "user" to user,
                            "rating" to user.ratingHistory,
                            "wealth" to user.wealthHistory,
                            "activeUser" to activeUser,
                        ),
                    ),
                )
            } else {
                call.respondRedirect("/menu")
            }
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

    authenticate("auth-session-admin-only") {
        get("/update/rating") {
            val countrysideDeltas =
                computeRatingDeltas(
                    studentService!!.getGroup(GroupType.Countryside),
                    ratingDeserializer.getCountrysideRating(),
                )
            val urbanDeltas =
                computeRatingDeltas(
                    studentService.getGroup(GroupType.Urban),
                    ratingDeserializer.getUrbanRating(),
                )

            call.respond(
                ThymeleafContent(
                    "update_rating",
                    mapOf(
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                        "countrysideStudents" to countrysideDeltas,
                        "urbanStudents" to urbanDeltas,
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/update/rating") {
            ratingDeserializer
                .getAllRatings()
                .forEach {
                    studentService!!.addRating(
                        Rating(
                            -1,
                            -1,
                            LocalDate.now().toKotlinLocalDate(),
                            it.solvedProblems,
                            it.rating,
                            it.solvedPercentage,
                            it.algebraPercentage,
                            it.geometryPercentage,
                            it.combinatoricsPercentage,
                        ),
                        it.lastName.trim() + " " + it.firstName.trim(),
                    )
                    studentService.updateRating(
                        it.lastName.trim() + " " + it.firstName.trim(),
                        it.rating,
                        it.solvedProblems,
                    )
                }
            call.respondRedirect("/update/rating")
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

    authenticate("auth-session-admin-only") {
        get("/transactions/exercises/countryside") {
            val countryside = studentService!!.getGroup(GroupType.Countryside)
            call.respond(
                ThymeleafContent(
                    "exercises",
                    mapOf(
                        "students" to countryside,
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/transactions/exercises/countryside") {
            val formContent = call.receiveText()
            println(formContent)
            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()

            if (params.jsonValue("activityType") == "exercises") {
                for (id: String in params.keys) {
                    if (id == "activityType") {
                        continue
                    }
                    studentService!!.updateStudentExercised(
                        id = id.toInt(),
                        exercised = params[id].toString() == "true",
                    )
                }
            }

            if (params.jsonValue("activityType") == "curfew") {
                for (id: String in params.keys) {
                    if (id == "activityType") {
                        continue
                    }
                    studentService!!.updateStudentBeaten(id = id.toInt(), beaten = params[id].toString() == "true")
                }
            }

            call.respond(HttpStatusCode.Accepted)
        }
    }

    authenticate("auth-session-admin-only") {
        get("/transactions/transactions/countryside") {
            val countryside = studentService!!.getGroup(GroupType.Countryside)
            call.respond(
                ThymeleafContent(
                    "transactions",
                    mapOf(
                        "students" to countryside,
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        get("/transactions/transactions/urban") {
            val urban = studentService!!.getGroup(GroupType.Urban)
            call.respond(
                ThymeleafContent(
                    "transactions",
                    mapOf(
                        "students" to urban,
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/transactions/transactions") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()
            println(params)
            val transactionName = params.jsonValue("transactionName")
            for (login: String in params.keys) {
                if (login == "transactionName") {
                    continue
                }
                val size = params.jsonValue(login).toInt()
                val studentId = DbStudentService.getStudentByLogin(login)!!.id
                DbTransactionService.makeTransaction(
                    Transaction(
                        id = 0,
                        studentId = studentId,
                        size = size,
                        date =
                            Clock.System.now().toLocalDateTime(
                                TimeZone.currentSystemDefault(),
                            ),
                        description = transactionName,
                    ),
                )
            }

            call.respond(HttpStatusCode.Accepted)
        }
    }

    get("/") {
        call.respondRedirect("/menu")
    }

    authenticate("auth-session-admin-only") {
        get("/event") {
            call.respond(
                ThymeleafContent(
                    "event_workshop",
                    mapOf(
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                    ),
                ),
            )
        }
    }

    authenticate("auth-session-admin-only") {
        post("/event/new") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()

            val event =
                Event(
                    -1,
                    params.jsonValue("eventName"),
                    params.jsonValue("timeAndPlace"),
                    params.jsonValue("eventDescription"),
                )

            eventService!!.addEvent(event)
            call.respond(HttpStatusCode.Accepted)
        }
    }

    authenticate("auth-session") {
        post("/profile/investing") {
            val formContent = call.receiveText()
            val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()

            val id = params["userId"].toString().toInt()
            val login = params.jsonValue("login")
            if (login != call.principal<Session>()?.login) {
                call.respond(HttpStatusCode.Unauthorized)
            }
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
}

private fun Map<String, JsonElement>.jsonValue(key: String): String {
    val quoted = this[key].toString()
    return quoted.substring(1, quoted.length - 1)
}
