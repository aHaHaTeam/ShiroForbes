package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.shiroforbes.login.Session
import ru.shiroforbes.model.Semester
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.RatingService
import ru.shiroforbes.service.StudentService
import ru.shiroforbes.service.UserService

fun Routing.ratingRoutes(
    ratingService: RatingService,
    studentService: StudentService,
    userService: UserService,
    ratingLoaderService: RatingLoaderService,
) {
    authenticate("auth-session-at-least-teacher") {
        get("/update/rating") {
            val countrysideDeltasDeferred =
                async { computeRatingDeltas(studentService, ratingLoaderService.getCountrysideRatingSemester2()) }

            val urbanDeltasDeferred =
                async { computeRatingDeltas(studentService, ratingLoaderService.getUrbanRatingSemester2()) }

            call.respond(
                ThymeleafContent(
                    "update_rating",
                    mapOf(
                        "user" to (userService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                        "countrysideStudents" to countrysideDeltasDeferred.await(),
                        "urbanStudents" to urbanDeltasDeferred.await(),
                    ),
                ),
            )
        }
    }
    authenticate("auth-session-admin-only") {
        val updateRatingScope = CoroutineScope(Dispatchers.Default)
        post("/update/countryside/rating") {
            updateRatingScope.launch {
                val rating = ratingLoaderService.getCountrysideRating()
                val ratingSemester2 = ratingLoaderService.getCountrysideRatingSemester2()
                updateRating(ratingService, rating, Semester.Semesters12)
                updateRating(ratingService, ratingSemester2, Semester.Semester2)
            }
            call.respondRedirect("/update/rating")
        }

        post("/update/urban/rating") {
            updateRatingScope.launch {
                val rating = ratingLoaderService.getUrbanRating()
                val ratingSemester2 = ratingLoaderService.getUrbanRatingSemester2()
                updateRating(ratingService, rating, Semester.Semesters12)
                updateRating(ratingService, ratingSemester2, Semester.Semester2)
            }
            call.respondRedirect("/update/rating")
        }
    }
}
