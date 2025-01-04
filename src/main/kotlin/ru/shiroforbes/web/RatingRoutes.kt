package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.shiroforbes.login.Session
import ru.shiroforbes.model.GroupType
import ru.shiroforbes.modules.googlesheets.RatingDeserializer
import ru.shiroforbes.service.DbRatingService.updateGroupAll
import ru.shiroforbes.service.DbUserService

fun Routing.ratingRoutes(ratingDeserializer: RatingDeserializer) {
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
            runBlocking {
                val rating = ratingDeserializer.getCountrysideRating()
                launch { updateRating(rating) }
                launch { updateGroupAll(rating.map { it.name() }, GroupType.Countryside) }
                launch { call.respondRedirect("/update/rating") } // TODO make it fast
            }
        }

        post("/update/urban/rating") {
            val rating = ratingDeserializer.getUrbanRating()
            launch { updateRating(rating) }
            launch { updateGroupAll(rating.map { it.name() }, GroupType.Urban) }
            launch { call.respondRedirect("/update/rating") }
        }
    }
}
