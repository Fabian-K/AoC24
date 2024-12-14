package de.kajzar.aoc24.day13

import de.kajzar.aoc24.Day
import de.kajzar.aoc24.split

fun main() {
    val machines = Day(13)
        .input()
        .readLines()
        .split { it.isEmpty() }
        .map {
            val a = it[0].extractDelta()
            val b = it[1].extractDelta()

            val price = it[2].extractTarget()

            Machine(a, b, price)
        }

    // part 1
    machines
        .mapNotNull { it.solve() }
        .sumOf { (a, b) -> a * 3 + b }
        .also { println(it) }

    // part 2
    machines
        .map { it.copy(price = it.price.copy(it.price.first + 10000000000000, it.price.second + 10000000000000)) }
        .mapNotNull { it.solve() }
        .sumOf { (a, b) -> a * 3 + b }
        .also { println(it) }
}

data class Machine(
    val a: Pair<Int, Int>,
    val b: Pair<Int, Int>,
    val price: Position,
)

typealias Position = Pair<Long, Long>

private fun Double.isWhole(): Boolean = this % 1 == 0.0

private fun Machine.solve(): Pair<Long, Long>? {
    // TIL: Cramer's Rule
    val p_x = price.first.toDouble()
    val p_y = price.second.toDouble()
    val a_x = a.first
    val a_y = a.second
    val b_x = b.first
    val b_y = b.second

    val a = (p_x * b_y - p_y * b_x) / (a_x * b_y - a_y * b_x)
    val b = (a_x * p_y - a_y * p_x) / (a_x * b_y - a_y * b_x)

    return if (a.isWhole() && b.isWhole()) {
        a.toLong() to b.toLong()
    } else {
        null
    }
}

fun String.extractDelta(): Pair<Int, Int> {
    val x = this.substringAfter("X+").substringBefore(",").toInt()
    val y = this.substringAfter("Y+").substringBefore(",").toInt()
    return x to y
}

fun String.extractTarget(): Position {
    val x = this.substringAfter("X=").substringBefore(",").toLong()
    val y = this.substringAfter("Y=").substringBefore(",").toLong()
    return x to y
}