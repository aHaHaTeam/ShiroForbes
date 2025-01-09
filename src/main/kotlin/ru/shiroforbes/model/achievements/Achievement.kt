package ru.shiroforbes.model.achievements

import kotlinx.datetime.LocalDateTime

class Achievement(
    val title: String,
    val description: String,
    val icon: String,
    val date: LocalDateTime,
    val record: Int,
)
