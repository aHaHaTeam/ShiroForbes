package ru.shiroforbes.jobs

import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import ru.shiroforbes.service.DbStudentService
import ru.shiroforbes.service.GroupService
import ru.shiroforbes.service.StudentService

class DailyResetExercise(
    val groupService: GroupService? = null,
    val studentService: StudentService? = DbStudentService,
) : Job {
    override fun execute(context: JobExecutionContext?) {
        runBlocking {
            studentService?.updateAllStudentsExercised(false)
        }
    }
}

class DailyResetBeat(
    val groupService: GroupService? = null,
    val studentService: StudentService? = DbStudentService,
) : Job {
    override fun execute(context: JobExecutionContext?) {
        runBlocking {
            studentService?.updateAllStudentsBeaten(false)
        }
    }
}
