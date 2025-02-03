package ru.shiroforbes.modules.googlesheets

interface TableParser<T> {
    fun parse(table: List<List<String>>): List<T>

    fun joinAndParse(tables: List<List<List<String>>>): List<T> {
        val shrankTables = tables.map { table ->
            val width = table.maxOf { it.size }
            table.takeWhile { it.size == width }
        }.reduce { table1, table2 ->
            table1.zip(table2).map { it.first + it.second }
        }
        return parse(shrankTables)
    }
}

