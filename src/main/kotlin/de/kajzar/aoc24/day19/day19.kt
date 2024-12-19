package de.kajzar.aoc24.day19

import de.kajzar.aoc24.Day
import de.kajzar.aoc24.split

private fun main() {
    val (towels, patterns) = Day(19)
        .input()
        .readLines()
        .split { it.isEmpty() }
        .let { (tList, patterns) -> tList.single().split(", ") to patterns }

    patterns
        .count { p -> allCombinationsFor(p, towels) > 0 }
        .also { println(it) }

    patterns
        .sumOf { p -> allCombinationsFor(p, towels) }
        .also { println(it) }
}

private val knownCombinations = mutableMapOf<String, Long>()

private fun allCombinationsFor(targetPattern: String, towels: List<String>): Long =
    knownCombinations.getOrPut(targetPattern) {
        towels
            .filter { t -> targetPattern.startsWith(t) }
            .sumOf { t ->
                val remaining = targetPattern.substringAfter(t)
                if (remaining == "")
                    1
                else
                    allCombinationsFor(remaining, towels)
            }
    }