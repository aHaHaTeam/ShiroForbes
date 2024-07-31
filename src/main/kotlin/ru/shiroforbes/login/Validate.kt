@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.login

import io.ktor.server.auth.*

public fun validate(
    login: String,
    password: String,
): Boolean {
    if (login == "admin" && password == "admin") {
        return true
    }
    return false
}

public fun isAdmin(user: UserIdPrincipal?): Boolean = true
