package ru.shiroforbes.database

import org.jetbrains.exposed.sql.*

object StudentRatings : Table("student_rating") {
    val student = reference("student_id", Students)
    val rating = reference("rating_id", Ratings)
    override val primaryKey = PrimaryKey(student, rating)
}

object StudentWealth : Table("student_wealth") {
    val student = reference("student_id", Students)
    val wealth = reference("wealth_id", Wealths)
    override val primaryKey = PrimaryKey(student, wealth)
}

object StudentTransaction : Table("student_transaction") {
    val student = reference("student_id", Students)
    val transaction = reference("transaction_id", Transactions)
    override val primaryKey = PrimaryKey(student, transaction)
}
