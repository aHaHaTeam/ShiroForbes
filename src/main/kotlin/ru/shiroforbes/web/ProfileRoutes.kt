package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import ru.shiroforbes.login.Session
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.StudentService
import ru.shiroforbes.service.UserService

fun Routing.profileRoutes(
    userService: UserService,
    studentService: StudentService,
    ratingLoaderService: RatingLoaderService
) {
    authenticate("auth-session-no-redirect") {
        get("/profile/{login}") {
            var activeUser: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    activeUser = userService.getUserByLogin(call.principal<Session>()!!.login) ?: 0
                }
            }
            val tmp = userService.getUserByLogin(call.parameters["login"]!!) ?: return@get
            if (tmp.hasAdminRights) {
                call.respondRedirect("/menu")
            }
            val user = studentService.getStudentByLogin(call.parameters["login"]!!)

            val urbanRating = ratingLoaderService.getUrbanRating()
            val countrysideRating = ratingLoaderService.getCountrysideRating()
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
