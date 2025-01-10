package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.shiroforbes.login.Session
import ru.shiroforbes.model.Rights
import ru.shiroforbes.model.User
import ru.shiroforbes.service.UserService

fun Routing.menuRoutes(userService: UserService) {
    get("/") {
        call.respondRedirect("/menu")
    }

    authenticate("auth-session-no-redirect") {
        get("/menu") {
            var user: Any = 0
            if (call.principal<Session>() != null) {
                if (call.principal<Session>()!!.login != "") {
                    user = userService.getUserByLogin(call.principal<Session>()?.login!!) ?: 0
                }
            }
            if (user == 0) {
                call.respondRedirect("/login")
                return@get
            }
            user as User
            if (user.rights == Rights.Student) {
                call.respondRedirect("/profile/${user.login}")
                return@get
            }
            call.respondRedirect("/update/rating")
        }
    }
}