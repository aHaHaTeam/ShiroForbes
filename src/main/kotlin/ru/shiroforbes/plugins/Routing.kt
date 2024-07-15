package ru.shiroforbes.plugins

import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        staticFiles("/static", File("src/main/resources/static/"))
        get("/") {
            call.respondText("Hello World!")
        }
//        get("/rating") {
//            call.respondFile(File("src/main/resources/static/rating.html"))
//        }
    }
}
