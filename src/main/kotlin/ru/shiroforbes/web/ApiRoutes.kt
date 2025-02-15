package ru.shiroforbes.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import ru.shiroforbes.auth.generateToken
import ru.shiroforbes.auth.hasRights
import ru.shiroforbes.auth.sameStudent
import ru.shiroforbes.auth.userRights
import ru.shiroforbes.config
import ru.shiroforbes.model.Rights
import ru.shiroforbes.model.Semester
import ru.shiroforbes.model.Student
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.RatingService
import ru.shiroforbes.service.StudentService
import ru.shiroforbes.service.UserService

@Serializable
data class UserJson(
    val id: Int,
    val name: String,
)

fun Routing.apiRoutes(
    userService: UserService,
    studentService: StudentService,
    ratingLoaderService: RatingLoaderService,
    ratingService: RatingService,
) {
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
        call.respond(HttpStatusCode.OK, mapOf("token" to token, "rights" to rights.name))
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
                async {
                    computeRatingDeltas(
                        studentService,
                        ratingLoaderService.getRating(config.googleSheetsConfig.countrysideRatingRangesSemester2),
                    )
                }
            call.respond(countrysideDeltasDeferred.await())
        }

        get("/api/rating/urban") {
            val principal = call.principal<JWTPrincipal>()
            if (!(hasRights(principal, Rights.Admin) || hasRights(principal, Rights.Teacher))) {
                call.respond(HttpStatusCode.Forbidden)
            }
            val urbanDeltasDeferred =
                async {
                    computeRatingDeltas(
                        studentService,
                        ratingLoaderService.getRating(config.googleSheetsConfig.urbanRatingRangesSemester2),
                    )
                }
            call.respond(urbanDeltasDeferred.await())
        }

        get("/api/profile/{login}") {
            val principal = call.principal<JWTPrincipal>()
            if (!(
                    hasRights(principal, Rights.Admin) ||
                        hasRights(principal, Rights.Teacher) ||
                        sameStudent(
                            principal,
                            call.parameters["login"]!!,
                        )
                )
            ) {
                call.respond(HttpStatusCode.Forbidden)
            }
            val profile = userService.getUserByLogin(call.parameters["login"]!!) ?: return@get

            if (profile.rights != Rights.Student) {
                call.respondRedirect("/menu")
            }
            profile as Student

            val ratings = ratingService.getRatings(profile.login)
            val numberOfPeople = studentService.getNumberOfStudentsInGroup(profile.group)

            val response =
                JsonObject(
                    mapOf(
                        "numberOfPeople" to JsonPrimitive(numberOfPeople),
                        "user" to Json.encodeToJsonElement(profile),
                        "ratings" to Json.encodeToJsonElement(ratings),
                    ),
                )
            call.respond(HttpStatusCode.OK, response)
        }

        val updateRatingScope = CoroutineScope(Dispatchers.Default)
        post("/api/rating/update/countryside") {
            // TODO check for rights
            updateRatingScope.launch {
                val rating = ratingLoaderService.getRating(config.googleSheetsConfig.countrysideRatingRanges)
                val ratingSemester2 = ratingLoaderService.getRating(config.googleSheetsConfig.countrysideRatingRangesSemester2)
                updateRating(ratingService, rating, Semester.Semesters12)
                updateRating(ratingService, ratingSemester2, Semester.Semester2)
            }
            call.respond(HttpStatusCode.OK)
        }

        post("/api/rating/update/urban") {
            updateRatingScope.launch {
                val rating = ratingLoaderService.getRating(config.googleSheetsConfig.urbanRatingRanges)
                val ratingSemester2 = ratingLoaderService.getRating(config.googleSheetsConfig.urbanRatingRangesSemester2)
                updateRating(ratingService, rating, Semester.Semesters12)
                updateRating(ratingService, ratingSemester2, Semester.Semester2)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}
