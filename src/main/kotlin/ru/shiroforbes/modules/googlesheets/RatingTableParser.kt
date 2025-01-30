package ru.shiroforbes.modules.googlesheets

import ru.shiroforbes.model.achievements.Series

class RatingTableParser : TableParser<List<Series>> {
    override fun parse(table: List<List<String>>): List<List<Series>> =
        table.drop(1).map { row ->
            var previousSeriesNumber = -1
            var grades = mutableListOf<Float>()
            val series = mutableListOf<Series>()
            row.zip(table[0]).forEach { (seriesNumberString, gradeString) ->
                val grade = parseGrade(gradeString) ?: return@forEach
                val seriesNumber = seriesNumberString.toIntOrNull() ?: return@forEach
                if (previousSeriesNumber != seriesNumber && previousSeriesNumber != -1) {
                    series.add(Series(previousSeriesNumber, grades))
                    grades = mutableListOf()
                }
                previousSeriesNumber = seriesNumber
                grades.add(grade)
            }
            return@map series.toList()
        }

    private fun parseGrade(grade: String): Float? {
        if (grade.contains(",")) {
            return grade.replace(",", ".").toFloatOrNull()
        }
        return grade.toFloatOrNull()
    }
}