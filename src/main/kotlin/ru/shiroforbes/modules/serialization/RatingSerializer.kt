package ru.shiroforbes.modules.serialization

import ru.shiroforbes.model.Group
import java.io.File

interface RatingSerializer {
    fun serialize(group: Group): File
}
