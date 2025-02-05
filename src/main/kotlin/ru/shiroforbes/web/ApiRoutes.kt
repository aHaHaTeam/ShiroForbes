package ru.shiroforbes.web

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import ru.shiroforbes.modules.googlesheets.RatingLoaderService
import ru.shiroforbes.service.StudentService

fun Routing.apiRoutes(
    studentService: StudentService,
    ratingLoaderService: RatingLoaderService,
) {
    get("/api/rating/get/countryside") {
        val countrysideDeltasDeferred =
            async { computeRatingDeltas(studentService, ratingLoaderService.getCountrysideRatingSemester2()) }
        call.respond(countrysideDeltasDeferred.await())
        println(countrysideDeltasDeferred.await().size)
    }

    get("/api/rating/get/urban") {
        val urbanDeltasDeferred =
            async { computeRatingDeltas(studentService, ratingLoaderService.getUrbanRatingSemester2()) }
        call.respond(urbanDeltasDeferred.await())
    }
}
