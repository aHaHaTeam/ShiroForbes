package ru.shiroforbes.web

import ru.shiroforbes.model.Student

data class TransactionUtil(
    val id: Int,
    val student: Student,
    val size: Int,
    val time: String,
    val date: String,
    val description: String,
)
