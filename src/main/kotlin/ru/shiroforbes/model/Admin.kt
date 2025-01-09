package ru.shiroforbes.model

class Admin(
    var name: String,
    login: String,
) : User(login, Rights.Admin)
