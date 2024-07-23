package ru.shiroforbes.model

// Dataclass representing a single transaction
data class Transaction(
    val id: Int,
    val studentId: Int,
    val size: Int,
    val description: String,
)
