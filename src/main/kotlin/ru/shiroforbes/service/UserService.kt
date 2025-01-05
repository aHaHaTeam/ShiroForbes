package ru.shiroforbes.service

import ru.shiroforbes.model.User

interface UserService {
    suspend fun getPasswordByLogin(login: String): String?
    suspend fun getUserByLogin(login: String): User?
}