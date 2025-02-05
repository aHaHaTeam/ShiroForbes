package ru.shiroforbes.model

import kotlinx.serialization.Serializable

@Serializable
class Admin(
    var name: String,
    override val login: String,
) : User() {
    override val rights: Rights = Rights.Admin
}
