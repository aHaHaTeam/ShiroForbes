package ru.shiroforbes.model

// represents student or admin
open class User(
    val name: String,
    val login: String,
    val password: String,
    val HasAdminRights: Boolean
) {


}