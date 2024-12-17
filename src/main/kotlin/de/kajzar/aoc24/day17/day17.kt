package de.kajzar.aoc24.day17

import de.kajzar.aoc24.Day
import de.kajzar.aoc24.split
import kotlin.math.floor
import kotlin.math.pow

private fun main() {
    val c = Day(17)
        .input()
        .readLines()
        .split { it.isEmpty() }
        .let { (registers, program) ->
            val (a, b, c) = registers.map { it.substringAfter(": ") }
                .map { it.toInt() }

            val ops = program.single().substringAfter(": ")
                .split(",")
                .map { it.toInt() }

            Computer(a, b, c, ops)
        }

    // part 1
    c.execute()
        .also { println(it) }

    // part 2:
    // - only last 3 bits are determine next output
    // - build possible numbers in reverse
    c.program.reversed().fold(listOf("")) { potentialBinNumbers, nextNumber ->
        potentialBinNumbers.flatMap { binNumber ->
            val minNum = "${binNumber}000".binToLong()
            val maxNum = "${binNumber}111".binToLong()

            (minNum..maxNum)
                .filter { c -> nextOutput(c) == nextNumber }
                .map { c -> c.toBin() }
        }
    }
        .minOf { it.binToLong() }
        .also { println(it) }
}

data class Computer(
    var a: Int,
    var b: Int,
    var c: Int,
    val program: List<Int>,
    var ip: Int = 0,
    val output: MutableList<Int> = mutableListOf(),
)

private fun Computer.execute(): String {
    while (true) {
        if (ip !in program.indices)
            return output.joinToString(",")

        when (val opcode = program[ip]) {
            0 -> a = floor(a / 2.0.pow(comboOperand())).toInt()
            1 -> b = b.xor(literalOperand())
            2 -> b = comboOperand() % 8
            3 -> if (a != 0) {
                ip = literalOperand()
                continue
            }

            4 -> b = b.xor(c)
            5 -> output.add(comboOperand() % 8)
            6 -> b = floor(a / 2.0.pow(comboOperand())).toInt()
            7 -> c = floor(a / 2.0.pow(comboOperand())).toInt()
            else -> error("Unknown opcode $opcode")
        }
        ip += 2
    }

}

private fun Computer.literalOperand(): Int = program[ip + 1]

private fun Computer.comboOperand(): Int {
    return when (val operand = program[ip + 1]) {
        in 0..3 -> operand
        4 -> a
        5 -> b
        6 -> c
        else -> error("Invalid combo operand $operand")
    }
}


fun String.binToLong(): Long = this.toULong(radix = 2).toLong()
fun Long.toBin(): String = this.toULong().toString(radix = 2)

/*
Program:
- (2,4) // B = A % 8
- (1,1) // B = B XOR 1
- (7,5) // C = A / 2^B
- (4,4) // B = B XOR C
- (1,4) // B = B XOR 4
- (0,3) // A = A / 8
- (5,5) // output B % 8
- (3,0) // exit if A=0, otherwise repeat
 */
private fun nextOutput(a: Long): Int {
    var b = (a % 8) xor 1
    b = b xor floor((a / (2.0.pow(b.toInt())))).toLong() xor 4
    return (b % 8).toInt()
}
