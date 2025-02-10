package ru.shiroforbes.model

import kotlinx.serialization.Serializable

@Serializable
enum class Rights(
    val power: Int,
) {
    Admin(2),
    Teacher(1),
    Student(0),
}
