package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class UserJson(
    val id: Int,
    val name: String,
)

fun Routing.testRoutes() {
    get("/api/users") {
        val users = listOf(UserJson(1, "Vika"), UserJson(2, "Lёnya"))
        call.respond(users)
    }
}
