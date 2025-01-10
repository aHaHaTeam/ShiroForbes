package ru.shiroforbes.model

enum class Rights(val power: Int) {
    Admin(2),
    Teacher(1),
    Student(0),
}