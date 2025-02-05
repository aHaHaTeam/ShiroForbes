package ru.shiroforbes.model

import kotlinx.serialization.Serializable

@Serializable
class Teacher(
    var name: String,
    override val login: String,
) : User() {
    override val rights: Rights
        get() = Rights.Teacher
}
