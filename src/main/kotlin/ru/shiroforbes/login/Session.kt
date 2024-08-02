package ru.shiroforbes.login

import io.ktor.server.auth.Principal

data class Session(
    val login: String,
    val password: String,
) : Principal
