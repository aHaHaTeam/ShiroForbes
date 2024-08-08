@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.login

import kotlinx.coroutines.runBlocking
import ru.shiroforbes.service.DbAdminService
import ru.shiroforbes.service.DbUserService
import java.security.MessageDigest

fun isAdmin(user: String?): Boolean = user == "admin"

@OptIn(ExperimentalStdlibApi::class)
fun validUser(
    login: String,
    password: String,
): Boolean {
    return runBlocking {
        val hashedPassword = DbUserService.getUserByLogin(login)?.password ?: return@runBlocking false
        return@runBlocking password == hashedPassword
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun validAdmin(
    login: String,
    password: String,
): Boolean {
    return runBlocking {
        val hashedPassword = DbAdminService.getAdminByLogin(login)?.password ?: return@runBlocking false
        return@runBlocking password == hashedPassword
    }
}

fun sha256(password: String): ByteArray {
    val sha = MessageDigest.getInstance("SHA-256")
    sha.update(password.toByteArray())
    return sha.digest()
}

fun knownPasswords(): Map<String, ByteArray> = mapOf("admin" to sha256("admin"))
