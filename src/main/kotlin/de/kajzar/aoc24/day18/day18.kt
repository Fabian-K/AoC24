package de.kajzar.aoc24.day18

import de.kajzar.aoc24.Day

private fun main() {
    val input: List<Position> = Day(18)
        .input()
        .readLines()
        .map { it.split(",").map { e -> e.toInt() }.let { (x, y) -> x to y } }

    val dimension = 70
    val start: Position = 0 to 0
    val end: Position = dimension to dimension

    // part 1
    val blocksPart1 = input.take(1024)
    bfs(
        start = start,
        end = end,
        neighbors = { p ->
            Direction.entries.map { p.neighbor(it) }
                .filter { it.x() in 0..dimension && it.y() in 0..dimension }
                .filter { it !in blocksPart1 }
        }
    )
        ?.let { it.size - 1 /* -> steps */ }
        ?.let { println(it) }

    // part 2
    (0..input.count()).reversed()
        .first { i ->
            val blocksPart2 = input.take(i)
            val path = bfs(
                start = start,
                end = end,
                neighbors = { p ->
                    Direction.entries.map { p.neighbor(it) }
                        .filter { it.x() in 0..dimension && it.y() in 0..dimension }
                        .filter { it !in blocksPart2 }
                }
            )

            path != null
        }
        .let { input[it] }
        .let { (x, y) -> println("$x,$y") }
}

private fun bfs(
    start: Position,
    end: Position,
    neighbors: (Position) -> List<Position>,
): List<Position>? {
    val queue = mutableListOf(start)
    val visited = mutableSetOf(start)
    val parent = mutableMapOf<Position, Position>()

    while (queue.isNotEmpty()) {
        val e = queue.removeFirst()
        if (e == end) {
            return generateSequence(e) { c -> parent[c] }
                .toList()
                .reversed()
        }
        neighbors(e)
            .filter { it !in visited }
            .forEach { n ->
                visited.add(n)
                parent[n] = e
                queue.add(n)
            }
    }
    return null
}

private typealias Position = Pair<Int, Int>

private fun Position.x() = first
private fun Position.y() = second

private enum class Direction { N, E, S, W }

private fun Position.neighbor(direction: Direction): Position = when (direction) {
    Direction.N -> x() to y() - 1
    Direction.E -> x() + 1 to y()
    Direction.S -> x() to y() + 1
    Direction.W -> x() - 1 to y()
}