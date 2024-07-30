@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes

import io.ktor.server.testing.*
import ru.shiroforbes.config.configureApp
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testRoot() =
        testApplication {
            application {
                configureApp()
            }
        }
}
