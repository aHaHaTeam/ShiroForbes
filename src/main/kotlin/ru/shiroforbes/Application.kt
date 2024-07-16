@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.shiroforbes.config.configureApp

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureApp()
}
