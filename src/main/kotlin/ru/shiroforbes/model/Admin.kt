package ru.shiroforbes.model

import ru.shiroforbes.database.GroupType

class Admin(
    val id: Int,
    name: String = "",
    login: String = "",
    password: String = "",
    group: GroupType,
) : User(name, login, password, group, true)
