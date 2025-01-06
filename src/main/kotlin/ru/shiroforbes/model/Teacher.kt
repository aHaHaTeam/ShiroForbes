package ru.shiroforbes.model

class Teacher(
    val name: String = "",
    login: String = "",
) : User(login, Rights.Teacher)
