package ru.shiroforbes.service

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shiroforbes.database.AdminTable
import ru.shiroforbes.database.StudentTable
import ru.shiroforbes.database.TeacherTable
import ru.shiroforbes.database.UserTable
import ru.shiroforbes.model.Admin
import ru.shiroforbes.model.Rights
import ru.shiroforbes.model.Teacher
import ru.shiroforbes.model.User

class DbUserService(
    private val database: Database,
    private val studentService: StudentService,
) : UserService {
    override fun getPasswordByLogin(login: String): Pair<String, Rights>? = transaction(database) {
        UserTable.selectAll().where(UserTable.login.eq(login)).singleOrNull()
    }?.run {
        this[UserTable.password] to this[UserTable.rights]
    }

    override suspend fun getUserByLogin(login: String): User? = transaction(database) {
        val usersQuery =
            UserTable.selectAll().where(UserTable.login.eq(login)).alias("users")
        val studentsQuery =
            StudentTable.selectAll().where(StudentTable.login.eq(login)).alias("students")
        val teachersQuery =
            TeacherTable.selectAll().where(TeacherTable.login.eq(login)).alias("teachers")
        val adminsQuery =
            AdminTable.selectAll().where(AdminTable.login.eq(login)).alias("admins")

        usersQuery.join(
            studentsQuery,
            joinType = JoinType.LEFT,
            onColumn = usersQuery[UserTable.login],
            otherColumn = studentsQuery[StudentTable.login]
        ).join(
            teachersQuery,
            joinType = JoinType.LEFT,
            onColumn = usersQuery[UserTable.login],
            otherColumn = teachersQuery[TeacherTable.login]
        ).join(
            adminsQuery,
            joinType = JoinType.LEFT,
            onColumn = usersQuery[UserTable.login],
            otherColumn = adminsQuery[AdminTable.login]
        ).selectAll().map {
            println(it)
            when (it[usersQuery[UserTable.rights]]) {
                Rights.Admin -> Admin(it[adminsQuery[AdminTable.name]], it[adminsQuery[AdminTable.login]])
                Rights.Teacher -> Teacher(it[teachersQuery[TeacherTable.name]], it[teachersQuery[TeacherTable.login]])
                Rights.Student -> studentService.getStudentByLogin(login)
            }
        }
    }.firstOrNull()
}
