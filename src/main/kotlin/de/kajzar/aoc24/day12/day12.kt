package de.kajzar.aoc24.day12

import de.kajzar.aoc24.Day

fun main() {
    val regions = Day(12)
        .input()
        .readLines()
        .map { it.toCharArray().toList() }
        .extractRegions()

    // part 1
    regions
        .sumOf { it.computeBorders().count() * it.area() }
        .also(::println)

    // part 2
    regions
        .sumOf { it.computeBorders().countSides() * it.area() }
        .also(::println)
}

private typealias Map = List<List<Char>>
private typealias Position = Pair<Int, Int>
private typealias Region = List<Position>

private fun Region.area() = count().toLong()

private fun Region.computeBorders(): Set<Pair<Position, Side>> {
    val borders = mutableSetOf<Pair<Position, Side>>()
    for (position in this) {
        for (side in Side.entries) {
            val n = position.neighborToSide(side)
            if (n !in this) {
                borders += n to side
            }
        }
    }
    return borders
}

private fun Set<Pair<Position, Side>>.countSides(): Int {
    // merge adjacent straight borders
    val borderToDo = toMutableList()

    var sides = 0
    while (borderToDo.isNotEmpty()) {
        val border = borderToDo.removeLast()
        sides++

        // continue in direction
        var checkBorder = border
        do {
            val nextBorder = checkBorder.first.neighborToSide(checkBorder.second.nextClockwise()) to border.second
            if (nextBorder !in borderToDo)
                break

            borderToDo.remove(nextBorder)
            checkBorder = nextBorder
        } while (true)

        // continue counter direction
        checkBorder = border
        do {
            val nextBorder =
                checkBorder.first.neighborToSide(checkBorder.second.nextCounterClockwise()) to border.second
            if (nextBorder !in borderToDo)
                break

            borderToDo.remove(nextBorder)
            checkBorder = nextBorder
        } while (true)
    }
    return sides
}

private fun Map.extractRegions(): List<Region> {
    val visited = mutableSetOf<Position>()
    val regions = mutableListOf<Region>()

    forEach { a, pos ->
        if (pos in visited) return@forEach

        visited += pos

        // expand region
        val region = mutableListOf(pos)
        neighbors(pos)
            .filter { (_, b) -> a == b }
            .forEach { (next, _) ->
                val queue = mutableListOf(next)
                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                    if (current in visited) continue
                    visited += current
                    region.add(current)
                    neighbors(current)
                        .filter { (_, b) -> a == b }
                        .forEach { (next, _) ->
                            queue.add(next)
                        }
                }
            }
        regions.add(region)
    }
    return regions
}

private fun Map.neighbors(p: Position): List<Pair<Position, Char?>> {
    return Side.entries
        .map { side ->
            val neighborPos = p.neighborToSide(side)
            neighborPos to get(neighborPos)
        }
}

private fun Position.neighborToSide(side: Side): Position {
    val (x, y) = this
    return when (side) {
        Side.TOP -> x to y - 1
        Side.RIGHT -> x + 1 to y
        Side.BOTTOM -> x to y + 1
        Side.LEFT -> x - 1 to y
    }
}

private fun Map.get(p: Position): Char? {
    val (x, y) = p
    return this.getOrNull(y)?.getOrNull(x)
}

private fun Map.forEach(block: (Char, Position) -> Unit) {
    forEachIndexed { y, row ->
        row.forEachIndexed { x, value ->
            block(value, x to y)
        }
    }
}

private enum class Side { TOP, RIGHT, BOTTOM, LEFT }

private fun Side.nextClockwise(): Side = when (this) {
    Side.TOP -> Side.RIGHT
    Side.RIGHT -> Side.BOTTOM
    Side.BOTTOM -> Side.LEFT
    Side.LEFT -> Side.TOP
}

private fun Side.nextCounterClockwise(): Side = when (this) {
    Side.TOP -> Side.LEFT
    Side.RIGHT -> Side.TOP
    Side.BOTTOM -> Side.RIGHT
    Side.LEFT -> Side.BOTTOM
}