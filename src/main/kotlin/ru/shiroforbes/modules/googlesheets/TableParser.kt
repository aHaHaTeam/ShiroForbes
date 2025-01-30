package ru.shiroforbes.modules.googlesheets

interface TableParser<T> {
    fun parse(table: List<List<String>>): List<T>

    fun joinAndParse(tables: List<List<List<String>>>): List<T> {
        val widths = tables.map { table -> table.maxOf { it.size } }
        val shrankTables = tables.mapIndexed { i, table -> table.takeWhile { it.size == widths[i] } }
            .reduce { a, b -> a.zip(b).map { it.first + it.second } }
        return parse(shrankTables)
    }
}

