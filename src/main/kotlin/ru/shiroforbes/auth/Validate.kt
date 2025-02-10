@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.runBlocking
import ru.shiroforbes.config
import ru.shiroforbes.model.Rights
import ru.shiroforbes.service.AdminService
import ru.shiroforbes.service.UserService
import java.util.*

fun validUser(
    userService: UserService,
    login: String,
    password: String,
): Boolean {
    return runBlocking {
        val hashedPassword = userService.getPasswordByLogin(login) ?: return@runBlocking false
        return@runBlocking password == hashedPassword.first
    }
}

fun userRights(
    userService: UserService,
    login: String,
    password: String,
): Rights? {
    val (hashedPassword, rights) = userService.getPasswordByLogin(login) ?: return null
    if (hashedPassword == password) return rights
    return null
}

fun validAdmin(
    adminService: AdminService,
    login: String,
    password: String,
): Boolean {
    return runBlocking {
        val hashedPassword = adminService.getPasswordByLogin(login) ?: return@runBlocking false
        return@runBlocking password == hashedPassword
    }
}

fun generateToken(
    login: String,
    rights: Rights,
): String =
    JWT
        .create()
        .withIssuer("ktor-server")
        .withClaim("login", login)
        .withClaim("rights", rights.name)
        .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000))
        .sign(Algorithm.HMAC256(config.authConfig.secretKey))

fun hasRights(
    principal: JWTPrincipal?,
    rights: Rights,
): Boolean {
    val userRights = principal?.payload?.getClaim("rights")?.asString()
    return userRights == rights.name
}

fun sameStudent(
    principal: JWTPrincipal?,
    login: String,
): Boolean {
    val userLogin = principal?.payload?.getClaim("login")?.asString()
    return userLogin == login
}
