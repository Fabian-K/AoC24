package de.kajzar.aoc24

class Day(private val nr: Int) {
    fun input() =
        this::class.java.getResourceAsStream("/$nr.txt")?.bufferedReader() ?: error("Input missing")
    fun testInput() =
        this::class.java.getResourceAsStream("/$nr-test.txt")?.bufferedReader() ?: error("Input missing")
}
