package ru.shiroforbes.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
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

    suspend fun getAllTransactions(): List<Transaction> = throw NotImplementedError() // TODO

    suspend fun getAllStudentTransactions(studentId: Int): List<Transaction> = throw NotImplementedError() // TODO

    suspend fun SendMoneyByCondition(
        students: List<Student>,
        amount: Int,
        condition: Student.() -> Boolean
    ): Unit
}

object DbTransactionService : TransactionService {
    init {
        Database.connect(
            "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?prepareThreshold=0",
            driver = "org.postgresql.Driver",
            user = "postgres.lsbuufukrknwuixafuuu",
            password = "shiroforbes239",
        )
    }

    private fun daoToTransaction(
        dao: TransactionDAO,
        studentId: Int,
    ): Transaction = Transaction(dao.id.value, studentId, dao.size, dao.date, dao.description)

    private fun getStudentId(transactionId: Int): Int =
        StudentTransaction
            .select(StudentTransaction.student)
            .where { StudentTransaction.transaction eq transactionId }
            .limit(1)
            .first()[StudentTransaction.student]
            .value

    override suspend fun makeTransaction(transaction: Transaction) {
        val tansactionId = Transactions.insertAndGetId {
            it[Transactions.size] = transaction.size
            it[Transactions.date] = transaction.date
            it[Transactions.description] = transaction.description
        }
        StudentTransaction.insert {
            it[StudentTransaction.transaction] = tansactionId
            it[StudentTransaction.student] = transaction.studentId
        }
    }

    override suspend fun getTransaction(id: Int): Transaction? {
        return transaction {
            val dao = TransactionDAO.findById(id) ?: return@transaction null
            val studentId = getStudentId(id)
            return@transaction daoToTransaction(dao, studentId)
        }
    }

    override suspend fun getAllTransactions(): List<Transaction> =
        TransactionDAO.all().map { daoToTransaction(it, getStudentId(it.id.value)) }

    override suspend fun getAllStudentTransactions(studentId: Int): List<Transaction> =
        Transactions.innerJoin(StudentTransaction).selectAll().where { Students.id eq studentId }.map {
            Transaction(
                it[Transactions.id].value,
                studentId,
                it[Transactions.size],
                it[Transactions.date],
                it[Transactions.description],
            )
        }

    override suspend fun SendMoneyByCondition(
        students: List<Student>,
        amount: Int,
        condition: Student.() -> Boolean
    ) {
        students.forEach {
            if (condition(it)) {
                val transaction = Transaction(
                    0,
                    it.id,
                    amount,
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    "successfully exercised"
                )
                makeTransaction(transaction)
            }
        }
    }
}
