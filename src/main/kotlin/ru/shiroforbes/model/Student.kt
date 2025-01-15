package ru.shiroforbes.model

class Student(
    val name: String,
    val group: GroupType,
    login: String,
    val score: Int,
    val total: Float,
) : User(login, Rights.Student)
