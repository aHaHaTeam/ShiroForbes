@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.config

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import ru.shiroforbes.service.GroupService
import ru.shiroforbes.web.rating

fun Application.configureApp() {
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
        rating(GroupService())
    }
}
