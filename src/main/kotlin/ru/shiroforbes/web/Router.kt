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
import io.ktor.util.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.login.Session
import ru.shiroforbes.login.isAdmin
import ru.shiroforbes.login.validUser
import ru.shiroforbes.model.User
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import ru.shiroforbes.service.DbStudentService
import ru.shiroforbes.service.DbUserService
import java.io.File

@OptIn(FormatStringsInDatetimeFormats::class, InternalAPI::class)
fun Routing.routes(
    // studentService: StudentService? = null,
    ratingDeserializer: RatingDeserializer,
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
            if (user == 0) {
                call.respondRedirect("/login")
            }
            user as User
            if (user.HasAdminRights != true) {
                call.respondRedirect("/profile/${user.login}")
            }
            call.respondRedirect("/update/rating")
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
            call.response.status(HttpStatusCode.Unauthorized)
            call.respondText("Invalid username or password")
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

    authenticate("auth-session-no-redirect") {
        get("/profile/{login}") {
            var activeUser: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    activeUser = DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0
                }
            }
            val tmp = DbUserService.getUserByLogin(call.parameters["login"]!!)
            if (tmp == null) {
                return@get
            }
            if (tmp.HasAdminRights) {
                call.respondRedirect("/menu")
            }
            val user = DbStudentService.getStudentByLoginSeason2(call.parameters["login"]!!)
            if (user == null) {
                return@get
                // TODO()
            }
            if (activeUser != user.login) {
                call.respond(
                    ThymeleafContent(
                        "profile",
                        mapOf(
                            "user" to user,
                            "activeUser" to activeUser,
                        ),
                    ),
                )
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
                    ratingDeserializer.getCountrysideRating(),
                )
            val urbanDeltas =
                computeRatingDeltas(
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
            updateRating(ratingDeserializer.getCountrysideRating())
            call.respondRedirect("/update/rating")
        }
    }

    authenticate("auth-session-admin-only") {
        post("/update/urban/rating") {
            updateRating(ratingDeserializer.getUrbanRating())
            call.respondRedirect("/update/rating")
        }
    }

    get("/") {
        call.respondRedirect("/menu")
    }
}

private fun Map<String, JsonElement>.jsonValue(key: String): String {
    val quoted = this[key].toString()
    return quoted.substring(1, quoted.length - 1)
}
