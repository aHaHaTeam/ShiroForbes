package ru.shiroforbes.model

class StudentStat(
    val name: String,
    val group: Boolean,
    login: String,
    val score: Int,
    val total: Float,
) : User(login, Rights.Student)