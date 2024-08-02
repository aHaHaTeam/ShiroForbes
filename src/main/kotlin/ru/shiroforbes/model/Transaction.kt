package ru.shiroforbes.model

import kotlinx.datetime.LocalDateTime

/**
 * Dataclass representing a single transaction
 */
data class Transaction(
    val id: Int,
    val studentId: Int,
    val size: Int,
    val date: LocalDateTime,
    val description: String,
)
