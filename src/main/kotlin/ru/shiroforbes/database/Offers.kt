package ru.shiroforbes.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Offers : IntIdTable("offers", "offer_id") {
    val name = varchar("name", 100)
    val description = varchar("description", 1000)
    val price = integer("price")
}

class OfferDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<OfferDAO>(Offers)

    var name by Offers.name
    var description by Offers.description
    var price by Offers.price
}
