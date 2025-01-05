@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.login

import kotlinx.coroutines.runBlocking
import ru.shiroforbes.service.AdminService
import ru.shiroforbes.service.UserService
import java.security.MessageDigest

fun validUser(
    userService: UserService,
    login: String,
    password: String,
): Boolean {
    return runBlocking {
        val hashedPassword = userService.getPasswordByLogin(login) ?: return@runBlocking false
        return@runBlocking password == hashedPassword
    }
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

fun sha256(password: String): ByteArray {
    val sha = MessageDigest.getInstance("SHA-256")
    sha.update(password.toByteArray())
    return sha.digest()
}

fun knownPasswords(): Map<String, ByteArray> = mapOf("admin" to sha256("admin"))
