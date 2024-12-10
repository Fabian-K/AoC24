package de.kajzar.aoc24.day10

import de.kajzar.aoc24.Day

fun main() {
    val map: Map = Day(10)
        .input()
        .readLines()
        .map { it.toCharArray().map { c -> c.digitToInt() } }

    val trailheads = map.findAll { it == 0 }

    val paths = trailheads
        .associateWith { t -> map.pathsToTop(t) }

    // part 1
    paths.map { (_, v) -> v.map { it.last() }.distinct().count() }
        .sum()
        .also { println(it) }

    // part 2
    paths.map { (_, v) -> v.distinct().count() }
        .sum()
        .also { println(it) }
}

private fun Map.pathsToTop(start: Position): List<Path> {
    val queue = ArrayDeque(
        listOf(listOf(start))
    )
    val result = mutableListOf<Path>()

    while (queue.isNotEmpty()) {
        val path = queue.removeFirst()

        val position = path.last()
        val value = get(position)!!

        if (value == 9) { // end of trail
            result.add(path)
            continue
        }

        neighbors(position)
            .filter { (_, v) -> v == value + 1 }
            .forEach { (next, _) ->
                queue.add(path + next)
            }
    }
    return result
}

private typealias Map = List<List<Int>>
private typealias Position = Pair<Int, Int>
private typealias Path = List<Position>

private fun Map.findAll(pred: (Int) -> Boolean): List<Position> {
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

private fun Map.neighbors(p: Position): List<Pair<Position, Int>> {
    val (x, y) = p
    return listOf(
        x to y - 1,
        x to y + 1,
        x - 1 to y,
        x + 1 to y,
    )
        .mapNotNull { neighborPosition ->
            get(neighborPosition)
                ?.let { neighborPosition to it }
        }
}

private fun Map.get(p: Position): Int? {
    val (x, y) = p
    return this.getOrNull(y)?.getOrNull(x)
}
