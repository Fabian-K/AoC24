package de.kajzar.aoc24

class Day(private val nr: Int) {
    fun input() =
        this::class.java.getResourceAsStream("/$nr.txt")?.bufferedReader() ?: error("Input missing")
    fun testInput() =
        this::class.java.getResourceAsStream("/$nr-test.txt")?.bufferedReader() ?: error("Input missing")
}

fun <T> List<T>.split(pred: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var next = mutableListOf<T>()
    forEach { l ->
        if (pred(l)) {
            result.add(next.toList())
            next = mutableListOf()
        } else {
            next.add(l)
        }
    }
    result.add(next)
    return result
}