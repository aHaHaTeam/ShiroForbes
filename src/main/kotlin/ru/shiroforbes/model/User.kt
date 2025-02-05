package ru.shiroforbes.model

import kotlinx.serialization.Serializable

// represents student or admin
@Serializable
sealed class User  {
    abstract val login: String
    abstract val rights: Rights

    override fun equals(other: Any?): Boolean =
        when (other) {
            is User -> (this.login == other.login)
            is Int -> other != 0
            else -> false
        }

    override fun hashCode(): Int = javaClass.hashCode()
}
