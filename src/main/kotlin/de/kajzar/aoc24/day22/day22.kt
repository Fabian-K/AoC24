package de.kajzar.aoc24.day22

import de.kajzar.aoc24.Day
import kotlin.math.floor

private fun main() {
    val monkeys = Day(22)
        .input()
        .readLines()
        .map { monkey(it.toInt()) }

    // part 1
    monkeys
        .sumOf { it.numbers.last().toLong() }
        .also { println(it) }

    // part 2
    val sumBySequence = mutableMapOf<List<Int>, Int>()
    monkeys.forEach { m ->
        m.priceChangeSequences().forEach { (i, seq) ->
            val price = m.prices[i + 4]
            if (price > 0) {
                val c = sumBySequence.getOrDefault(seq, 0)
                sumBySequence[seq] = c + price
            }
        }
    }
    sumBySequence.maxBy { it.value }
        .also { (_, v) -> println(v) }
}

private data class Monkey(
    val numbers: List<Int>,
    val prices: List<Int>,
    val priceChanges: List<Int>,
)

private fun Monkey.priceChangeSequences(): List<IndexedValue<List<Int>>> = priceChanges
    .windowed(4)
    .withIndex()
    .distinctBy { it.value }

private fun monkey(start: Int): Monkey {
    val numbers = generateSequence(start) { s0 ->
        val s1 = s0 * 64
        val s2 = s1.mix(s0)
        val s3 = s2.prune()
        val s4 = floor(s3 / 32.0).toInt()
        val s5 = s4.mix(s3)
        val s6 = s5.prune()
        val s7 = s6 * 2048
        val s8 = s7.mix(s6)
        s8.prune()
    }
        .take(2000 + 1)
        .toList()

    val prices = numbers.map { n -> n.toString().last().digitToInt() }
    val priceChanges = prices.windowed(2) { (a, b) -> b - a }

    return Monkey(
        numbers = numbers,
        prices = prices,
        priceChanges = priceChanges,
    )
}

fun Int.prune() = mod(16777216)
fun Int.mix(o: Int) = xor(o)