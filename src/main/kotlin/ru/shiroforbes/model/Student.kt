package ru.shiroforbes.model

// Dataclass representing student
data class Student(
    val id: Int,
    val name: String,
    val login: String,
    val password: String,
    val rating: Int,
    val wealth: Int,
)
