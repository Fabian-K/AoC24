package de.kajzar.aoc24.day2

import de.kajzar.aoc24.Day
import kotlin.math.absoluteValue

fun main() {
    val reports = Day(2)
        .input()
        .readLines()
        .map { line ->
            line.split(" ").map { it.toInt() }
        }

    // part 1
    reports.count { it.deltas().isSafe() }
        .let { println(it) }


    // part 2
    reports.count { r ->
        val d = r.deltas()
        val mutations = r.indices.map { omit ->
            r.mapIndexed { index, i -> if (index == omit) null else i }
                .filterNotNull()
        }
        d.isSafe() || mutations.any { it.deltas().isSafe() }
    }
        .let { println(it) }

}

fun List<Int>.deltas() = windowed(2).map { (a, b) -> b - a }

fun List<Int>.isSafe(): Boolean {
    return (this.all { it > 0 } || this.all { it < 0 })
            && this.map { it.absoluteValue }.all { it in 1..3 }
}