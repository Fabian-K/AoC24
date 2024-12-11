package de.kajzar.aoc24.day11

import de.kajzar.aoc24.Day

fun main() {
    val input: List<Stone> = Day(11)
        .input()
        .readLines().single()
        .split(" ")
        .map { it.toLong() }

    // part 1
    input
        .sumOf { it.blinkCount(25) }
        .also { println(it) }

    // part 2
    input
        .sumOf { it.blinkCount(75) }
        .also { println(it) }
}

private typealias Stone = Long

private fun Stone.blink(): List<Stone> {
    if (this == 0L)
        return listOf(1L)

    if (digits() % 2 == 0) {
        val (a, b) = this.split()
        return listOf(a, b)
    }

    return listOf(this * 2024)
}

private fun Stone.digits() = toString().count()

private fun Stone.split(): Pair<Stone, Stone> {
    val asString = this.toString()
    return asString.substring(0, asString.length / 2).toLong() to asString.substring(asString.length / 2).toLong()
}

private val cache = mutableMapOf<Pair<Stone, Int>, Long>()

private fun Stone.blinkCount(count: Int): Long = cache.getOrPut((this to count)) {
    if (count == 0) return@getOrPut 1

    blink().sumOf { it.blinkCount(count - 1) }
}