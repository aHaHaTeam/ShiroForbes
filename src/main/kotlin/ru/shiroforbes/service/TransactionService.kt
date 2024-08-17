@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.service

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.config
import ru.shiroforbes.database.StudentTransaction
import ru.shiroforbes.database.Students
import ru.shiroforbes.database.TransactionDAO
import ru.shiroforbes.database.Transactions
import ru.shiroforbes.model.Student
import ru.shiroforbes.model.Transaction

/**
 * Service for accessing the transaction storage.
 * It is very likely that this interface will be changed/expanded depending on future requirements.
 */
interface TransactionService {
    suspend fun makeTransaction(transaction: Transaction): Unit = throw NotImplementedError()

    suspend fun getTransaction(id: Int): Transaction? = throw NotImplementedError() // TODO

    suspend fun deleteTransaction(id: Int): Unit = throw NotImplementedError()

    suspend fun getAllTransactions(): List<Transaction> = throw NotImplementedError() // TODO

    suspend fun getAllStudentTransactions(studentId: Int): List<Transaction> = throw NotImplementedError() // TODO

    suspend fun sendMoneyByCondition(
        students: List<Student>,
        amount: Int,
        dateTime: LocalDateTime,
        description: String,
        condition: Student.() -> Boolean,
    ): Unit
}

object DbTransactionService : TransactionService {
    init {
        Database.connect(
            config.dbConfig.connectionUrl,
            config.dbConfig.driver,
            config.dbConfig.user,
            config.dbConfig.password,
        )
    }

    private fun daoToTransaction(
        dao: TransactionDAO,
        studentId: Int,
    ): Transaction = Transaction(dao.id.value, studentId, dao.size, dao.date, dao.description)

    private fun getStudentId(transactionId: Int): Int =
        transaction {
            StudentTransaction
                .select(StudentTransaction.student)
                .where { StudentTransaction.transaction eq transactionId }
                .limit(1)
                .first()[StudentTransaction.student]
                .value
        }

    override suspend fun makeTransaction(transaction: Transaction) {
        transaction {
            val transactionId =
                Transactions.insertAndGetId {
                    it[Transactions.size] = transaction.size
                    it[Transactions.date] = transaction.date
                    it[Transactions.description] = transaction.description
                }
            StudentTransaction.insert {
                it[StudentTransaction.transaction] = transactionId
                it[StudentTransaction.student] = transaction.studentId
            }
            Students.update({ Students.id eq transaction.studentId }) {
                it[wealth] = wealth + transaction.size
            }
        }
    }

    override suspend fun getTransaction(id: Int): Transaction? {
        return transaction {
            val dao = TransactionDAO.findById(id) ?: return@transaction null
            val studentId = getStudentId(id)
            return@transaction daoToTransaction(dao, studentId)
        }
    }

    override suspend fun deleteTransaction(id: Int) {
        transaction {
            StudentTransaction.deleteWhere { StudentTransaction.transaction eq id }
            Transactions.deleteWhere { Transactions.id eq id }
        }
    }

    override suspend fun getAllTransactions(): List<Transaction> =
        transaction {
            TransactionDAO.all().map { daoToTransaction(it, getStudentId(it.id.value)) }
        }

    override suspend fun getAllStudentTransactions(studentId: Int): List<Transaction> =
        transaction {
            Transactions.innerJoin(StudentTransaction).selectAll().where { StudentTransaction.student eq studentId }.map {
                Transaction(
                    it[Transactions.id].value,
                    studentId,
                    it[Transactions.size],
                    it[Transactions.date],
                    it[Transactions.description],
                )
            }
        }

    override suspend fun sendMoneyByCondition(
        students: List<Student>,
        amount: Int,
        dateTime: LocalDateTime,
        description: String,
        condition: Student.() -> Boolean,
    ) {
        students.forEach {
            if (condition(it)) {
                val transaction =
                    Transaction(
                        0,
                        it.id,
                        amount,
                        dateTime,
                        description,
                    )
                makeTransaction(transaction)
            }
        }
    }
}
