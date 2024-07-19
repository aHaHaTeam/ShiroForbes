package ru.shiroforbes.service

import ru.shiroforbes.model.Transaction

// Service for accessing the transaction storage.
// It is very likely that this interface will be changed/expanded depending on future requirements.
interface TransactionService {
    suspend fun getTransaction(id: Int): Transaction = throw NotImplementedError() // TODO

    suspend fun getAllTransactions(): List<Transaction> = throw NotImplementedError() // TODO

    suspend fun getAllStudentTransactions(studentId: Int): List<Transaction> = throw NotImplementedError() // TODO
}
