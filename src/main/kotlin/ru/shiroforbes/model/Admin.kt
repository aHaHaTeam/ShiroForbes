package ru.shiroforbes.model

class Admin(
    val name: String = "",
    login: String = "",
) : User(login, true)
