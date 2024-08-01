package ru.shiroforbes.model

class Admin(val id: Int,
            name: String = "",
            login: String = "",
            password: String = "",):User(name, login, password, true) {

}