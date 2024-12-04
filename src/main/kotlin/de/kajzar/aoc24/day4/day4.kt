package de.kajzar.aoc24.day4

import de.kajzar.aoc24.Day

fun main() {
    val grid: Grid = Day(4)
        .input()
        .readLines()
        .map { it.toCharArray() }

    val start = grid.findAll('X')

    // part 1
    start
        // "beams" in all directions
        .flatMap { p -> Direction.entries.map { dir -> grid.expand(p, dir, 4) } }
        .count { it == "XMAS" }
        .let(::println)

    // part 2
    grid.findAll('A')
        // expand to X
        .map { grid.expandToX(it) }
        .count { it.isMAS() }
        .let(::println)
}

private fun Grid.expandToX(centerPos: Pos): X {
    // diagonal 1
    val d1 = listOfNotNull(
        get(centerPos.move(Direction.NW)),
        get(centerPos),
        get(centerPos.move(Direction.SE)),
    )
        .joinToString(separator = "")

    // diagonal 2
    val d2 = listOfNotNull(
        get(centerPos.move(Direction.NE)),
        get(centerPos),
        get(centerPos.move(Direction.SW)),
    )
        .joinToString(separator = "")

    return X(d1, d2)
}

private data class X(val d1: String, val d2: String)

private fun X.isMAS() = d1.isMAS() && d2.isMAS()

private fun Grid.findAll(char: Char): List<Pos> = buildList {
    for ((y, line) in this@findAll.withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == char) add(x to y)
        }
    }
}

private typealias Grid = List<CharArray>
private typealias Pos = Pair<Int, Int>

private fun Grid.get(pos: Pos) = this.getOrNull(pos.second)?.getOrNull(pos.first)

private fun Grid.expand(pos: Pos, dir: Direction, steps: Int = 1) = buildList {
    add(this@expand.get(pos))

    var current = pos
    repeat(steps - 1) {
        current = current.move(dir)
        val char = this@expand.get(current) ?: return@buildList

        add(char)
    }
}.filterNotNull().joinToString(separator = "")

private enum class Direction {
    N, NE, E, SE, S, SW, W, NW
}

private fun Pos.move(dir: Direction): Pos {
    val (x, y) = this
    return when (dir) {
        Direction.N -> x to y - 1
        Direction.NE -> x + 1 to y - 1
        Direction.E -> x + 1 to y
        Direction.SE -> x + 1 to y + 1
        Direction.S -> x to y + 1
        Direction.SW -> x - 1 to y + 1
        Direction.W -> x - 1 to y
        Direction.NW -> x - 1 to y - 1
    }
}

private fun String.isMAS() = this == "MAS" || this == "MAS".reversed()