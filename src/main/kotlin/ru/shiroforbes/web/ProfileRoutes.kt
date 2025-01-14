package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import ru.shiroforbes.login.Session
import ru.shiroforbes.model.*
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.RatingService
import ru.shiroforbes.service.UserService

fun Routing.profileRoutes(
    userService: UserService,
    ratingService: RatingService,
    ratingLoaderService: RatingLoaderService,
) {
    authenticate("auth-session-no-redirect") {
        get("/profile/{login}") {
            var activeUser: User? = null
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    activeUser = userService.getUserByLogin(call.principal<Session>()!!.login)
                }
            }

            if (activeUser == null || (activeUser.rights == Rights.Student && activeUser.login != call.parameters["login"])) {
                return@get call.respond(ThymeleafContent("forbidden", mapOf()))
            }

            val profile =
                if (activeUser.login != call.parameters["login"]!!) {
                    userService.getUserByLogin(call.parameters["login"]!!) ?: return@get
                } else {
                    activeUser
                }

            if (profile.rights != Rights.Student) {
                call.respondRedirect("/menu")
            }
            profile as Student
            val ratings = ratingService.getRatings(profile.login)
            val urbanRating = ratingLoaderService.getUrbanRating()
            val countrysideRating = ratingLoaderService.getCountrysideRating()

            val numberOfPeople = if (profile.group == GroupType.Urban) urbanRating.size else countrysideRating.size

            call.respond(
                ThymeleafContent(
                    "profile",
                    mapOf(
                        "user" to profile,
                        "ratings" to (ratings[Semester.Semesters12] ?: listOf()),
                        "activeUser" to activeUser,
                        "numberOfPeople" to numberOfPeople,
                    ),
                ),
            )
        }
    }
}
