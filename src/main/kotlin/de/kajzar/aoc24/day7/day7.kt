package de.kajzar.aoc24.day7

import de.kajzar.aoc24.Day
import java.text.DecimalFormat

fun main() {
    val eqs = Day(7)
        .input()
        .readLines()
        .map { l ->
            val (result, parts) = l.split(": ")
            val numbers = parts.split(" ").map { it.toDouble() }
            Eq(result.toDouble(), numbers)
        }

    // part 1
    eqs
        .filter { eq ->
            val comb = generateCombinations(eq.parts.size, listOf(Op.ADD, Op.MUL))
            for (ops in comb) {
                if (eq.matchesResultUsing(ops)) return@filter true
            }
            false
        }
        .sumOf { it.result }
        .let { println("Result: ${it.toPlainString()}") }

    // part 2
    val combCache = mutableMapOf<Int, List<List<Op>>>()
    eqs
        .filter { eq ->
            val comb = combCache.getOrPut(eq.parts.size) { generateCombinations(eq.parts.size, Op.entries) }
            for (ops in comb) {
                if (eq.matchesResultUsing(ops)) return@filter true
            }
            false
        }
        .sumOf { it.result }
        .let { println("Result: ${it.toPlainString()}") }
}

data class Eq(val result: Double, val parts: List<Double>)

enum class Op { ADD, MUL, CON }

fun Eq.matchesResultUsing(ops: List<Op>): Boolean {
    var sum = parts[0]
    for (i in 1 until parts.size) {
        when (ops[i - 1]) {
            Op.ADD -> sum += parts[i]
            Op.MUL -> sum *= parts[i]
            Op.CON -> sum = "${sum.toPlainString()}${parts[i].toPlainString()}".toDouble()
        }
        if (sum > result) return false
    }
    return sum == result
}

fun generateCombinations(count: Int, options: List<Op>): List<List<Op>> {
    if (count == 1) return listOf(listOf())

    val result = mutableListOf<List<Op>>()
    for (op in options) {
        val subCombinations = generateCombinations(count - 1, options)
        for (subCombination in subCombinations) {
            result.add(listOf(op) + subCombination)
        }
    }
    return result
}

private val format = DecimalFormat("#")

fun Double.toPlainString() = format.format(this)!!