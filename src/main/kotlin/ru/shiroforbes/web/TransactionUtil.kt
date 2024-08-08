package ru.shiroforbes.web

import kotlinx.datetime.LocalDateTime
import ru.shiroforbes.model.Student

data class TransactionUtil(
    val id: Int,
    val student: Student,
    val size: Int,
    val date: LocalDateTime,
    val description: String,
)
