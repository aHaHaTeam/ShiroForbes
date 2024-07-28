package ru.shiroforbes.service

import ru.shiroforbes.model.Group

/**
 * Service for accessing the group storage.
 * It is very likely that this interface will be changed/expanded depending on future requirements.
 */
interface GroupService {
    suspend fun getAllGroups(): List<Group> = throw NotImplementedError() // TODO
}
