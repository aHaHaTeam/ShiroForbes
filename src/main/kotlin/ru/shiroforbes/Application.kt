@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.shiroforbes.config.DbConfig
import ru.shiroforbes.config.GoogleSheetsConfig
import ru.shiroforbes.config.RouterConfig
import ru.shiroforbes.config.configureApp

data class Config(
    val googleSheetsConfig: GoogleSheetsConfig,
    val routerConfig: RouterConfig,
    val dbConfig: DbConfig,
)

internal val config: Config by lazy {
    ConfigLoaderBuilder
        .default()
        .addResourceSource("/config.yaml")
        .build()
        .loadConfigOrThrow<Config>()
}

fun main(args: Array<String>) {
    if (args.contains("--init")) {
        ru.shiroforbes.database.main()
        return
    }
    runBlocking {
        launch {
            embeddedServer(Netty, port = 80, host = "0.0.0.0", module = Application::module)
                .start(wait = true)
        }
    }
}

fun Application.module() {
    configureApp(config)
}
