package ru.shiroforbes.model

import kotlinx.datetime.LocalDate

/**
 * Dataclass representing a single transaction
 */
data class Transaction(
    val id: Int,
    val studentId: Int,
    val size: Int,
    val date: LocalDate,
    val description: String,
)
