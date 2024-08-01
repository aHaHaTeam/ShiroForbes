package ru.shiroforbes.jobs

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.quartz.Job
import org.quartz.JobExecutionContext
import ru.shiroforbes.model.Student
import ru.shiroforbes.model.Transaction
import ru.shiroforbes.service.*


class DailyResetExercise(
    val groupService: GroupService? = null,
    private val studentService: StudentService? = DbStudentService,
    private val transactionService: TransactionService? = DbTransactionService,
) : Job {
    override fun execute(context: JobExecutionContext?) {
        runBlocking {
            transactionService!!.SendMoneyByCondition(studentService!!.getAllStudents(), amount = 50) {
                this.isExercised == true
            }
            studentService.updateAllStudentsExercised(false)
        }
    }
}

class DailyResetBeat(
    val groupService: GroupService? = null,
    private val studentService: StudentService? = DbStudentService,
    private val transactionService: TransactionService? = DbTransactionService,
) : Job {
    override fun execute(context: JobExecutionContext?) {
        runBlocking {
            transactionService!!.SendMoneyByCondition(studentService!!.getAllStudents(), amount = 50) {
                this.isBeaten == true
            }
            studentService.updateAllStudentsBeaten(false)
        }
    }
}
