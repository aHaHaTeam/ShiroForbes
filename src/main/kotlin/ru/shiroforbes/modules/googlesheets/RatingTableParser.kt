package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.model.achievements.Series

class RatingTableParser : TableParser<List<Series>> {
    override fun parse(table: List<List<String>>): List<List<Series>> {
        val numbersRow = table.first()
        return table.drop(1).map { row ->
            var previous: Int? = null
            val grades = mutableListOf<Float>()
            val series = mutableListOf<Series>()
            row.zip(numbersRow).forEach { (seriesNumberString, gradeString) ->
                val grade = parseGrade(gradeString) ?: return@forEach
                val seriesNumber = seriesNumberString.toIntOrNull() ?: return@forEach
                if (previous != seriesNumber && previous != null) {
                    series.add(Series(previous!!, grades))
                    grades.clear()
                }
                previous = seriesNumber
                grades.add(grade)
            }
            return@map series.toList()
        }
    }

    private fun parseGrade(grade: String): Float? =
        grade.replace(",", ".").toFloatOrNull()
}