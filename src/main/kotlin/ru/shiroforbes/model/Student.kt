package ru.shiroforbes.model

// Dataclass representing student
data class Student(
    val id: Int,
    val name: String,
    val login: String,
    val password: String,
    val rating: Int,
    val wealth: Int,
    val isExercised: Boolean?,
    val isBeaten: Boolean?
) {
    fun solved(): Int {
        return this.algSolved() + this.geomSolved() + this.combSolved()
    }

    fun algSolved(): Int {
        return 1
    }

    fun geomSolved(): Int {
        return 2
    }

    fun combSolved(): Int {
        return 3
    }
}
