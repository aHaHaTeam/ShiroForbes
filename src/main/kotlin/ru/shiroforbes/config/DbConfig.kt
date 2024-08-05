package ru.shiroforbes.config

data class DbConfig(
    val connectionUrl: String,
    val driver: String,
    val user: String,
    val password: String,
)
