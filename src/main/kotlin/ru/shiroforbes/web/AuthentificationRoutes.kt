package ru.shiroforbes.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.login.Session
import ru.shiroforbes.login.userRights
import ru.shiroforbes.model.Rights
import ru.shiroforbes.service.StudentService
import ru.shiroforbes.service.UserService

fun Routing.authenticationRoutes(studentService: StudentService, userService: UserService) {
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
        val login = params.jsonValue("login")
        val password = params.jsonValue("password")

        val rights = userRights(userService, login, password)

        if (rights == null) {
            call.response.status(HttpStatusCode.Unauthorized)
            call.respondText("Invalid username or password")
            return@post
        }

        call.sessions.set(Session(login, password))
        if (rights == Rights.Student) {
            val student = studentService.getStudentByLogin(login)
            call.respondRedirect("/profile/${student!!.login}")
        } else {
            call.respondRedirect("/menu")
        }
    }

    get("/logout") {
        call.sessions.set(Session("", ""))
        call.respondRedirect("/login")
    }
}

private fun Map<String, JsonElement>.jsonValue(key: String): String {
    val quoted = this[key].toString()
    return quoted.substring(1, quoted.length - 1)
}