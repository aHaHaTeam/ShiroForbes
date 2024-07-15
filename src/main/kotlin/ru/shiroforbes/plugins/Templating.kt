package ru.shiroforbes.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.WeakHashMap

fun Application.configureTemplating() {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    routing {
        get("/html-thymeleaf") {
            call.respond(ThymeleafContent("index", mapOf("user" to ThymeleafUser(1, "user1"))))
        }
        get("/rating") {
            call.respond(ThymeleafContent("rating", mapOf("allUsers" to users)))
        }
    }
}

val users = mutableListOf(
    ThymeleafUser(0,"Leo",0,-2),
    ThymeleafUser(1,"name0"),
    ThymeleafUser(1,"name0"),
    ThymeleafUser(1,"name0"),
)

data class ThymeleafUser(val id: Int, val name: String, val rating: Int= 1234, val wealth: Int = 239)
