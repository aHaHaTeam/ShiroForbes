@file:Suppress("ktlint:standard:no-wildcard-imports")

package ru.shiroforbes.jobs

import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import ru.shiroforbes.service.DbStudentService
import ru.shiroforbes.service.DbTransactionService
import ru.shiroforbes.service.StudentService
import ru.shiroforbes.service.TransactionService

class DailyResetExercise(
    private val studentService: StudentService? = DbStudentService,
    private val transactionService: TransactionService? = DbTransactionService,
) : Job {
    override fun execute(context: JobExecutionContext?) {
        runBlocking {
            transactionService!!.sendMoneyByCondition(studentService!!.getAllStudents(), amount = 50) {
                this.isExercised == true
            }
            studentService.updateAllStudentsExercised(false)
        }
    }
}

class DailyResetBeat(
    private val studentService: StudentService? = DbStudentService,
    private val transactionService: TransactionService? = DbTransactionService,
) : Job {
    override fun execute(context: JobExecutionContext?) {
        runBlocking {
            transactionService!!.sendMoneyByCondition(studentService!!.getAllStudents(), amount = 50) {
                this.isBeaten == true
            }
            studentService.updateAllStudentsBeaten(false)
        }
    }
}
