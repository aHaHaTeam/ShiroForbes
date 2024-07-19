package ru.shiroforbes.service

import ru.shiroforbes.model.Student

// Service for accessing the student storage.
// It is very likely that this interface will be changed/expanded depending on future requirements.
interface StudentService {
    suspend fun getStudent(id: Int): Student = throw NotImplementedError() // TODO

    suspend fun getAllStudents(): List<Student> = throw NotImplementedError() // TODO

    suspend fun updateStudent(id: Int): Student = throw NotImplementedError() // TODO
}
