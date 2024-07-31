@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes

import io.ktor.server.testing.*
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testRoot() =
        testApplication {
            application {
            }
        }
}
