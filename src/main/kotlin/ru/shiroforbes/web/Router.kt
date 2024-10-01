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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.login.Session
import ru.shiroforbes.login.isAdmin
import ru.shiroforbes.login.validUser
import ru.shiroforbes.model.*
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import ru.shiroforbes.modules.markdown.MarkdownConverter
import ru.shiroforbes.modules.serialization.RatingSerializer
import ru.shiroforbes.service.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime as JavaDateTime

@OptIn(FormatStringsInDatetimeFormats::class)
fun Routing.routes(
    studentService: StudentService? = null,
    eventService: EventService? = null,
    transactionService: TransactionService,
    ratingSerializer: RatingSerializer,
    ratingDeserializer: RatingDeserializer,
    markdownConverter: MarkdownConverter,
    routerConfig: RouterConfig,
) {
    staticFiles("/static", File("src/main/resources/static/"))

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

    authenticate("auth-session-no-redirect") {
        get("/menu") {
            var user: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    user = DbUserService.getUserByLogin(call.principal<Session>()?.login!!) ?: 0
                }
            }
            if (user == 0)
                {
                    call.respondRedirect("/login")
                }
            user as User
            if (user.HasAdminRights != true) {
                call.respondRedirect("/profile/${user.login}")
            }
            val events = eventService!!.getAllEvents()
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
                        "countrysideCampEvents" to events.filter { it.group == GroupType.Countryside },
                        "urbanCampEvents" to events.filter { it.group == GroupType.Urban },
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
                val transactions =
                    DbTransactionService
                        .getAllStudentTransactions(user.id)
                        .filter { it.date.toJavaLocalDateTime().isBefore(JavaDateTime.now()) }
                        .sortedByDescending { it.date }
                        .map {
                            TransactionUtil(
                                it.id,
                                user,
                                it.size,
                                it.date.format(LocalDateTime.Format { byUnicodePattern("HH:mm:ss") }),
                                it.date.format(LocalDateTime.Format { byUnicodePattern("dd.MM.yyyy") }),
                                it.description,
                            )
                        }
                call.respond(
                    ThymeleafContent(
                        "profile",
                        mapOf(
                            "investingAllowed" to false,
                            "user" to user,
                            "rating" to user.ratingHistory,
                            "wealth" to user.wealthHistory,
                            "activeUser" to activeUser,
                            "transactions" to transactions,
                        ),
                    ),
                )
            } else {
                call.respondRedirect("/menu")
            }
        }
    }

    get("/logout") {
        call.sessions.set(Session("", ""))
        call.respondRedirect("/login")
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
        post("/update/countryside/rating") {
            updateRating(studentService!!, ratingDeserializer.getCountrysideRating())
            call.respondRedirect("/update/rating")
        }
    }

    authenticate("auth-session-admin-only") {
        post("/update/urban/rating") {
            updateRating(studentService!!, ratingDeserializer.getUrbanRating())
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

    get("/") {
        call.respondRedirect("/menu")
    }
}

private fun Map<String, JsonElement>.jsonValue(key: String): String {
    val quoted = this[key].toString()
    return quoted.substring(1, quoted.length - 1)
}
