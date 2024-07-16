package ru.shiroforbes.service

import ru.shiroforbes.model.Group

// Service for accessing the group storage.
// It is very likely that this interface will be changed/expanded depending on future requirements.
class GroupService {
    suspend fun getGroup(id: Int): Group = throw NotImplementedError() // TODO

    suspend fun getAllGroups(): List<Group> = throw NotImplementedError() // TODO
}
