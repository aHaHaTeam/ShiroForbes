package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import kotlinx.coroutines.async
import ru.shiroforbes.login.Session
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import ru.shiroforbes.service.DbUserService

fun Routing.ratingRoutes(
    ratingDeserializer: RatingDeserializer,
) {
    authenticate("auth-session-admin-only") {
        get("/update/rating") {
            val countrysideDeltasDeferred = async { computeRatingDeltas(ratingDeserializer.getCountrysideRating()) }

            val urbanDeltasDeferred = async { computeRatingDeltas(ratingDeserializer.getUrbanRating()) }

            call.respond(
                ThymeleafContent(
                    "update_rating",
                    mapOf(
                        "user" to (DbUserService.getUserByLogin(call.principal<Session>()!!.login) ?: 0),
                        "countrysideStudents" to countrysideDeltasDeferred.await(),
                        "urbanStudents" to urbanDeltasDeferred.await(),
                    ),
                ),
            )
        }

        post("/update/countryside/rating") {
            val rating = ratingDeserializer.getCountrysideRating()
            updateRating(rating)
            updateGroup(rating, GroupType.Countryside)
            call.respondRedirect("/update/rating")
        }

        post("/update/urban/rating") {
            val rating = ratingDeserializer.getUrbanRating()
            updateRating(rating)
            updateGroup(rating, GroupType.Urban)
            call.respondRedirect("/update/rating")
        }
    }
}