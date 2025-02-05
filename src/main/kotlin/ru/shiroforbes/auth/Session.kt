package ru.shiroforbes.auth

import io.ktor.server.auth.*

data class Session(
    val login: String,
    val password: String,
) : Principal
