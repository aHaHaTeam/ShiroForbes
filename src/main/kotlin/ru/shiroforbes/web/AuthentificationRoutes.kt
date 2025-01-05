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
import ru.shiroforbes.login.validAdmin
import ru.shiroforbes.login.validUser
import ru.shiroforbes.service.AdminService
import ru.shiroforbes.service.UserService

fun Routing.authenticationRoutes(adminService: AdminService, userService: UserService) {
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
        if (!validUser(userService, name, password)) {
            call.response.status(HttpStatusCode.Unauthorized)
            call.respondText("Invalid username or password")
            return@post
        }
        call.sessions.set(Session(name, password))
        if (validAdmin(adminService, name, password)) {
            call.respondRedirect("/menu")
            return@post
        }
        val user = userService.getUserByLogin(name)
        call.respondRedirect("/profile/${user!!.login}")
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