package ru.shiroforbes.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import ru.shiroforbes.auth.generateToken
import ru.shiroforbes.auth.hasRights
import ru.shiroforbes.auth.userRights
import ru.shiroforbes.model.Rights
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.StudentService
import ru.shiroforbes.service.UserService

@Serializable
data class UserJson(
    val id: Int,
    val name: String,
)


fun Routing.apiRoutes(userService: UserService, studentService: StudentService, ratingLoaderService: RatingLoaderService) {
    post("/api/login") {
        val formContent = call.receiveText()
        val params = (Json.parseToJsonElement(formContent) as JsonObject).toMap()
        val login = params.jsonValue("login")
        val password = params.jsonValue("password")

        val rights = userRights(userService, login, password)
        if (rights == null) {
            call.response.status(HttpStatusCode.Unauthorized)
            return@post
        }

        val token = generateToken(login, rights)
        call.respond(HttpStatusCode.OK, mapOf("token" to token))
    }



    authenticate("auth-jwt") {
        get("/api/users") {
            val principal = call.principal<JWTPrincipal>()
            if (!hasRights(principal, Rights.Admin)) {
                call.respond(HttpStatusCode.Forbidden)
            }

            val users = listOf(UserJson(1, "Vika"), UserJson(2, "Lёnya"))
            call.respond(users)
        }

        get("/api/rating/countryside") {
            val principal = call.principal<JWTPrincipal>()
            if (!(hasRights(principal, Rights.Admin) || hasRights(principal, Rights.Teacher))) {
                call.respond(HttpStatusCode.Forbidden)
            }
            val countrysideDeltasDeferred =
                async { computeRatingDeltas(studentService, ratingLoaderService.getCountrysideRatingSemester2()) }
            call.respond(countrysideDeltasDeferred.await())
            println(countrysideDeltasDeferred.await().size)
        }

        get("/api/rating/urban") {
            val principal = call.principal<JWTPrincipal>()
            if (!(hasRights(principal, Rights.Admin) || hasRights(principal, Rights.Teacher))) {
                call.respond(HttpStatusCode.Forbidden)
            }
            val urbanDeltasDeferred =
                async { computeRatingDeltas(studentService, ratingLoaderService.getUrbanRatingSemester2()) }
            call.respond(urbanDeltasDeferred.await())
        }

        get("/api/profile/{login}") {
            TODO("handle api request")
        }

        get("/api/ratings/{login}") {
            TODO("handle api request")
        }
    }
}
