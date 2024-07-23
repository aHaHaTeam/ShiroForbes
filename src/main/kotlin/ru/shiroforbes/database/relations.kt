package ru.shiroforbes.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.date

object StudentRatings : Table("student_rating") {
    val student = reference("student_id", Students)
    val rating = reference("rating_id", Ratings)
    override val primaryKey = PrimaryKey(student, rating)
}

object StudentWealth : Table("student_wealth") {
    val student = reference("student_id", Students)
    val date = date("date")
    override val primaryKey = PrimaryKey(student, date)
}

object StudentGroup : Table("student_group") {
    val student = reference("student_id", Students)
    val group = integer("group")
    override val primaryKey = PrimaryKey(student, group)
}

object StudentTransaction : Table("student_transaction") {
    val student = reference("student_id", Students)
    val transaction = reference("transaction_id", Transactions)
    override val primaryKey = PrimaryKey(student, transaction)
}
