package ru.shiroforbes.model

import kotlinx.datetime.LocalDate

class Wealth(
    val id: Int,
    val studentId: Int,
    val date: LocalDate,
    val wealth: Int,
)
