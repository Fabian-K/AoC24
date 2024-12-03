package de.kajzar.aoc24.day3

import de.kajzar.aoc24.Day

fun main() {
    val input = Day(3)
        .input()
        .readLines()
        .joinToString()

    val mulExpr = "mul\\(([0-9]{1,3}),([0-9]{1,3})\\)".toRegex()

    // part 1
    mulExpr
        .findAll(input)
        .sumOf { it.evaluate() }
        .also { println(it) }

    // part 2
    var remaining = input

    var enabled = true
    var sum = 0

    while (remaining.isNotEmpty()) {
        if (!enabled) {
            remaining = remaining.substringAfter("do()", "")
            enabled = true
            continue
        }

        val nextSwitchPos = remaining.indexOf("don't()")

        val nextMUL = mulExpr.find(remaining)
        val nextMULPos = nextMUL?.let { remaining.indexOf(it.value) } ?: -1

        if (nextSwitchPos < nextMULPos) {
            // apply switch
            remaining = remaining.substringAfter("don't()")
            enabled = false
            continue
        } else if (nextMUL != null) {
            // apply mul
            sum += nextMUL.evaluate()
            remaining = remaining.substringAfter(nextMUL.value)
        } else if (nextSwitchPos == -1 && nextMULPos == -1) {
            break
        }
    }

    println(sum)
}

private fun MatchResult.evaluate() = groupValues.drop(1)
    .map { it.toInt() }
    .reduce { acc, i -> acc * i }