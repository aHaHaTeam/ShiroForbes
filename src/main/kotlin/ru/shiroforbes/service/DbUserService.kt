package ru.shiroforbes.service

import ru.shiroforbes.model.User

class DbUserService(
    private val studentService: StudentService,
    private val adminService: AdminService,
) : UserService {
    override suspend fun getPasswordByLogin(login: String): String? {
        val student = studentService.getPasswordByLogin(login) ?: return adminService.getPasswordByLogin(login)
        return student
    }

    override suspend fun getUserByLogin(login: String): User? {
        val student = studentService.getStudentStatByLogin(login) ?: return adminService.getAdminByLogin(login)
        return student
    }
}
