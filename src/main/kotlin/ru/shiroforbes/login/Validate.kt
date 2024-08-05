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
        val savedPassword = DbUserService.getUserByLogin(login)?.password ?: return@runBlocking false
        val hashedPassword = md5(savedPassword).toHexString()
        return@runBlocking password == hashedPassword
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun validAdmin(
    login: String,
    password: String,
): Boolean {
    return runBlocking {
        val savedPassword = DbAdminService.getAdminByLogin(login)?.password ?: return@runBlocking false
        val hashedPassword = md5(savedPassword).toHexString()
        return@runBlocking password == hashedPassword
    }
}

fun md5(password: String): ByteArray {
    val md = MessageDigest.getInstance("MD5")
    md.update(password.toByteArray())
    return md.digest()
}

fun knownPasswords(): Map<String, ByteArray> = mapOf("admin" to md5("admin"))
