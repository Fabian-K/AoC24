package de.kajzar.aoc24.day6

import de.kajzar.aoc24.Day

fun main() {
    val map: Map = Day(6)
        .input()
        .readLines()
        .map { it.toCharArray().toList() }

    // part 1
    val start = map.findAll('^').single()
    val result = map.simulate()
        .also { println("Result: ${it.visited.size}") }

    // part 2
    val potentialBlockPositions = result.visited.minus(start)
    potentialBlockPositions
        .count { blockPos ->
            val updated = map.block(blockPos)
            updated.simulate().loop
        }
        .let { println("Result: $it") }
}

private fun Map.block(blockPos: Position): Map {
    val x = blockPos.first
    val y = blockPos.second

    val updated = this.toMutableList()
    updated[y] = updated[y].toMutableList().apply { set(x, '#') }

    return updated
}

private fun Map.simulate(): SimulationResult {
    var guard = findAll('^')
        .single()
    var guardDirection = Direction.UP

    val visited = mutableSetOf(guard to guardDirection)

    while (true) {
        val nextPos = guard.move(guardDirection)
        val nextTile = get(nextPos)

        if ((nextPos to guardDirection) in visited) {
            return SimulationResult(visited.map { it.first }.toSet(), true)
        }

        if (nextTile == '#') { // rotate
            guardDirection = guardDirection.rotate()
            continue
        }

        if (nextTile == null) { // out of map
            return SimulationResult(visited.map { it.first }.toSet(), false)
        }

        // move
        guard = nextPos
        visited.add(guard to guardDirection)
    }
}

private data class SimulationResult(
    val visited: Set<Position>,
    val loop: Boolean,
)

private typealias Map = List<List<Char>>

private typealias Position = Pair<Int, Int>

private fun Map.get(position: Position) = this.getOrNull(position.second)?.getOrNull(position.first)

private fun Map.findAll(char: Char): List<Position> {
    val result = mutableListOf<Position>()
    for (y in this.indices) {
        for (x in this[y].indices) {
            if (this[y][x] == char) {
                result.add(x to y)
            }
        }
    }
    return result
}

private enum class Direction {
    UP, RIGHT, DOWN, LEFT;
}

private fun Direction.rotate(): Direction {
    return when (this) {
        Direction.UP -> Direction.RIGHT
        Direction.RIGHT -> Direction.DOWN
        Direction.DOWN -> Direction.LEFT
        Direction.LEFT -> Direction.UP
    }
}

private fun Position.move(direction: Direction): Position {
    return when (direction) {
        Direction.UP -> first to second - 1
        Direction.RIGHT -> first + 1 to second
        Direction.DOWN -> first to second + 1
        Direction.LEFT -> first - 1 to second
    }
}