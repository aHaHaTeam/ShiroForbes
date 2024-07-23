package ru.shiroforbes.database

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Transactions : IntIdTable("transaction", "transaction_id") {
    val description: Column<String> = varchar("description", 200)
    val date: Column<LocalDate> = date("date")
    val size = integer("size")
}

class TransactionDAO(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<TransactionDAO>(Transactions)

    var description by Transactions.description
    var date by Transactions.date
    var size by Transactions.size
}
