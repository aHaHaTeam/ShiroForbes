package ru.shiroforbes.model

// represents student or admin
open class User(
    val login: String,
    val rights: Rights,
) {
    override fun equals(other: Any?): Boolean =
        when (other) {
            is User -> (this.login == other.login)
            is Int -> other != 0
            else -> false
        }

    override fun hashCode(): Int = javaClass.hashCode()
}
