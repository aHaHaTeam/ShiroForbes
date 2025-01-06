package ru.shiroforbes.model

class AdminStat(
    var name: String,
    login: String,
) : User(login, Rights.Admin)