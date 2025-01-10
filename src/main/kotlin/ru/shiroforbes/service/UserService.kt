package ru.shiroforbes.service

import ru.shiroforbes.model.Rights
import ru.shiroforbes.model.User

interface UserService {
    fun getPasswordByLogin(login: String): Pair<String, Rights>?
    suspend fun getUserByLogin(login: String): User?
}