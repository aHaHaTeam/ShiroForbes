package ru.shiroforbes.model

class TeacherStat(
    var name: String,
    login: String,
) : User(login, Rights.Admin)