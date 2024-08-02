package ru.shiroforbes.model

class Admin(
    val id: Int,
    name: String = "",
    login: String = "",
    password: String = "",
    group: GroupType,
) : User(name, login, password, group, true)
