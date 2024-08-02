@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.login

import java.security.MessageDigest

fun isAdmin(user: String?): Boolean = user == "admin"

fun validUser(
    login: String,
    password: String,
): Boolean = true

fun md5(password: String): ByteArray {
    val md = MessageDigest.getInstance("MD5")
    md.update(password.toByteArray())
    return md.digest()
}

fun knownPasswords(): Map<String, ByteArray> = mapOf("admin" to md5("admin"))
