package ru.shiroforbes.model

class Teacher(
    var name: String,
    login: String,
) : User(login, Rights.Teacher)
