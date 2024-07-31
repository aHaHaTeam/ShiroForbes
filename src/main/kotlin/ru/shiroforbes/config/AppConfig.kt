@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.config

import com.google.api.services.sheets.v4.SheetsScopes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import ru.shiroforbes.Config
import ru.shiroforbes.modules.googlesheets.GoogleSheetsApiConnectionService
import ru.shiroforbes.modules.googlesheets.GoogleSheetsService
import ru.shiroforbes.modules.googlesheets.RatingRow
import ru.shiroforbes.modules.serialization.RatingSerializer
import ru.shiroforbes.service.DbEventService
import ru.shiroforbes.service.DbStudentService
import ru.shiroforbes.web.routes

fun Application.configureApp(config: Config) {
    install(Thymeleaf) {
        setTemplateResolver(
            ClassLoaderTemplateResolver().apply {
                prefix = "templates/thymeleaf/"
                suffix = ".html"
                characterEncoding = "utf-8"
            },
        )
    }

    install(Routing) {
        routes(
            studentService = DbStudentService,
            ratingSerializer = RatingSerializer(),
            ratingDeserializer =
                GoogleSheetsService(
                    GoogleSheetsApiConnectionService(
                        "/googlesheets/credentials.json",
                        listOf(SheetsScopes.SPREADSHEETS_READONLY),
                    ),
                    config.googleSheetsConfig.ratingSpreadsheetId,
                    RatingRow::class,
                    config.googleSheetsConfig.ratingRanges,
                ),
            eventService = DbEventService,
            routerConfig = config.routerConfig,
        )
    }
}
