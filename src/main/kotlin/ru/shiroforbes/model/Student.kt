package ru.shiroforbes.model

import kotlinx.serialization.Serializable

@Serializable
class Student(
    val name: String,
    val group: Group,
    override val login: String,
    val score: Int,
    val total: Float,
) : User() {
    override val rights: Rights = Rights.Student
}
