package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import ru.shiroforbes.login.Session
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import ru.shiroforbes.service.DbStudentService
import ru.shiroforbes.service.DbUserService

fun Routing.profileRoutes(
    ratingDeserializer: RatingDeserializer,
) {
    authenticate("auth-session-no-redirect") {
        get("/profile/{login}") {
            var activeUser: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    activeUser = DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0
                }
            }
            val tmp = DbUserService.getUserByLogin(call.parameters["login"]!!) ?: return@get
            if (tmp.hasAdminRights) {
                call.respondRedirect("/menu")
            }
            val user = DbStudentService.getStudentByLoginSeason2(call.parameters["login"]!!)
            if (user == null) {
                return@get
                // TODO()
            }

            val urbanRating = ratingDeserializer.getUrbanRating()
            val countrysideRating = ratingDeserializer.getCountrysideRating()
            val numberOfPeople = if (user.group) urbanRating.size else countrysideRating.size
            if (activeUser != user.login) {
                call.respond(
                    ThymeleafContent(
                        "profile",
                        mapOf(
                            "user" to user,
                            "activeUser" to activeUser,
                            "numberOfPeople" to numberOfPeople,
                        ),
                    ),
                )
            }
        }
    }
}