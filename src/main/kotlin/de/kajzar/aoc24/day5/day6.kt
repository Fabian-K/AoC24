package de.kajzar.aoc24.day5

import de.kajzar.aoc24.Day
import de.kajzar.aoc24.split

fun main() {
    val inputParts = Day(5)
        .input()
        .readLines()
        .split { it.isBlank() }

    val order = inputParts.first()
        .map {
            val p = it.split("|")
            p.first().toInt() to p.last().toInt()
        }

    val pageOrders = inputParts.last()
        .flatMap { it.split("\n") }
        .map { l -> l.split(",").map { it.toInt() } }

    // part 1
    pageOrders
        .filter { it.isCorrectOrder(order) }
        .sumOf { it[(it.count() / 2)] }
        .let { println("Result: $it") }

    // part 2
    pageOrders
        .filter { !it.isCorrectOrder(order) }
        .map { p ->
            val rules = p.relevantRules(order)

            var pages = p
            while (true) {
                val swap = mutableListOf<Int>()

                for ((i, page) in pages.withIndex()) {
                    val pageRule = rules.getValue(page)
                    val actuallyBefore = pages.subList(0, i)
                    val actuallyAfter = pages.subList(i + 1, pages.size)

                    actuallyBefore.filter { it in pageRule.mustBeBefore }
                        .forEach {
                            swap.add(it)
                            swap.add(page)
                        }

                    actuallyAfter.filter { it in pageRule.mustBeAfter }
                        .forEach {
                            swap.add(it)
                            swap.add(page)
                        }
                }

                pages = pages.swap(swap.take(2))

                if (pages.isCorrectOrder(order))
                    return@map pages
            }
            error("")
        }
        .sumOf { it[(it.count() / 2)] }
        .let { println("Result: $it") }

}

private fun List<Int>.swap(swap: List<Int>): List<Int> {
    val a = swap.first()
    val b = swap.last()

    return buildList {
        this@swap.forEach { x ->
            when (x) {
                a -> add(b)
                b -> add(a)
                else -> add(x)
            }
        }
    }
}

private fun PrintTask.isCorrectOrder(order: List<Pair<Int, Int>>): Boolean {
    val rules = relevantRules(order)
    return isInRightOrder(rules)
}

private fun PrintTask.relevantRules(order: List<Pair<Int, Int>>): Map<Int, PageRule> {
    val rules = distinct().associateWith {
        val after = order.filter { (a, _) -> a == it }
            .map { it.second }
        val before = order.filter { (_, b) -> b == it }
            .map { it.first }

        PageRule(before, after)
    }
    return rules
}

data class PageRule(
    val mustBeAfter: List<Int>,
    val mustBeBefore: List<Int>,
)

typealias PrintTask = List<Int>

fun PrintTask.isInRightOrder(rules: Map<Int, PageRule>): Boolean {
    for ((i, page) in this.withIndex()) {
        val pageRule = rules.getValue(page)
        val actuallyBefore = this.subList(0, i)
        val actuallyAfter = this.subList(i + 1, this.size)

        val failA = actuallyBefore.any { p -> p in pageRule.mustBeBefore }
        if (failA)
            return false

        val failB = actuallyAfter.any { p -> p in pageRule.mustBeAfter }
        if (failB)
            return false
    }
    return true
}