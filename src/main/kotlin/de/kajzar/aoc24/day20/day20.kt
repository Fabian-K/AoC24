package de.kajzar.aoc24.day20

import de.kajzar.aoc24.Day

private fun main() {
    val map: Map = Day(20)
        .input()
        .readLines()
        .map { it.toCharArray().toList() }

    val start = map.findAll { it == 'S' }.single()
    val end = map.findAll { it == 'E' }.single()

    val track = bfs(
        start = start,
        end = end,
        neighbors = { p -> neighborsWithDistance(p, 1).filter { map.get(it) != '#' } }
    )!!

    val positionIndex = track.associateWith { track.indexOf(it) }

    track.flatMap { p ->
        val startIndex = positionIndex.getValue(p)
        neighborsWithDistance(p, 2)
            .filter { map.get(it) != '#' }
            .map { n ->
                val endIndex = positionIndex.getOrDefault(n, -1)
                Cheat(p, n, endIndex - startIndex - 2)
            }
            .filter { it.saved >= 100 }
            .distinct()
    }
        .count()
        .also { println(it) }

    track.flatMap { p ->
        val startIndex = positionIndex.getValue(p)
        (2..20).flatMap { cheatCount ->
            neighborsWithDistance(p, cheatCount)
                .filter { map.get(it) != '#' }
                .map { n ->
                    val endIndex = positionIndex.getOrDefault(n, -1)
                    Cheat(p, n, endIndex - startIndex - cheatCount)
                }
                .filter { it.saved >= 100 }
                .distinct()
        }
    }
        .count()
        .also { println(it) }
}

private data class Cheat(val start: Position, val end: Position, val saved: Int)

private fun neighborsWithDistance(p: Position, c: Int): Set<Position> = buildSet {
    val (x, y) = p
    (0..c).forEach { i ->
        val d = c - i
        add(x + i to y - d)
        add(x + i to y + d)
        add(x - i to y - d)
        add(x - i to y + d)
    }
}

private fun Map.findAll(pred: (Char) -> Boolean): List<Position> {
    val result = mutableListOf<Position>()

    forEachIndexed { y, row ->
        row.forEachIndexed { x, value ->
            if (pred(value)) {
                result.add(x to y)
            }
        }
    }

    return result
}

private typealias Map = List<List<Char>>
private typealias Position = Pair<Int, Int>

private fun Position.x() = first
private fun Position.y() = second

private fun Map.get(pos: Position) = getOrNull(pos.y())?.getOrNull(pos.x())

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