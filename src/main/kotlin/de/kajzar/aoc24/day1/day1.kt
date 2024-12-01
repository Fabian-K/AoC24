package de.kajzar.aoc24.day1

import de.kajzar.aoc24.Day

fun main() {
    val (left, right) = Day(1)
        .input()
        .readLines()
        .map { line ->
            val parts = line.split("   ")
            parts.first().toInt() to parts.last().toInt()
        }
        .let { lines ->
            val left = lines.map { it.first }
            val right = lines.map { it.second }
            left to right
        }

    // part 1
    left.sorted().zip(right.sorted())
        .sumOf { (a, b) ->
            if (a > b) {
                a - b
            } else {
                b - a
            }
        }
        .also { println(it) }

    // part 2
    left
        .sumOf { n -> right.count { it == n } * n }
        .also { println(it) }

}