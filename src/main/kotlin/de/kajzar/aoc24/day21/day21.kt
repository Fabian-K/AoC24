package de.kajzar.aoc24.day21

import de.kajzar.aoc24.Day
import kotlin.collections.component1
import kotlin.collections.component2

private fun main() {
    val codes = Day(21)
        .input()
        .readLines()
        .map { it.toCharArray().toList() }

    // part 1
    codes.sumOf { code -> code.computeMinLength(2) * code.numeric() }
        .also { println(it) }

    // part 2
    codes.sumOf { code -> code.computeMinLength(25) * code.numeric() }
        .also { println(it) }
}

private typealias Code = List<Char>

private fun Code.numeric() = filter { it.isDigit() }
    .joinToString(separator = "")
    .toInt()

private fun List<Char>.computeMinLength(robotCount: Int): Long {
    val robots = (0..robotCount).map { i -> Robot(i) }
        // chain
        .also { it.windowed(2).forEach { (a, b) -> a.nextRobot = b } }

    return robots.first()
        .minPressFor(this)
}


private data class Robot(
    private val level: Int,
) {
    private val _cache: MutableMap<List<Char>, Long> = mutableMapOf()

    var nextRobot: Robot? = null

    private val map = if (level == 0)
        """
        789
        456
        123
        #0A
    """.trimIndent().toMap()
    else """
        #^A
        <v>
    """.trimIndent().toMap()

    fun minPressFor(code: List<Char>): Long = _cache.getOrPut(code) {
        val (_, sum) = code.fold('A' to 0L) { (current, sum), next ->
            val possiblePaths = map.paths(current, next)
                .map { it.plus('A') }

            val nextSum = possiblePaths
                .minOf { p -> nextRobot?.minPressFor(p) ?: p.count().toLong() }

            next to (nextSum + sum)
        }
        sum
    }

}

private fun String.toMap(): Map = split("\n")
    .map { it.toCharArray().toList() }

private fun Map.paths(
    start: Char,
    end: Char,
    neighbors: (Position) -> List<Position> = { p ->
        buildList {
            val (x, y) = p
            add((x + 1 to y))
            add((x - 1 to y))
            add((x to y + 1))
            add((x to y - 1))
        }
            .filter { get(it) !in listOf(null, '#') }
    },
): List<List<Char>> {
    val paths = bfs(find(start), find(end), neighbors)
    return paths.map { p -> p.toDirections() }
}

private fun List<Position>.toDirections(): List<Char> {
    return this.windowed(2).map { (a, b) ->
        val dx = b.x() - a.x()
        val dy = b.y() - a.y()
        when {
            dx > 0 -> '>'
            dx < 0 -> '<'
            dy > 0 -> 'v'
            dy < 0 -> '^'
            else -> error("Invalid")
        }
    }
}

private typealias Map = List<List<Char>>
private typealias Position = Pair<Int, Int>

private fun Position.x() = first
private fun Position.y() = second

private fun Map.get(pos: Position) = getOrNull(pos.y())?.getOrNull(pos.x())

private fun Map.find(char: Char): Position {
    forEachIndexed { y, row ->
        row.forEachIndexed { x, value ->
            if (value == char) return (x to y)
        }
    }
    error("Not found")
}

private fun bfs(
    start: Position,
    end: Position,
    neighbors: (Position) -> List<Position>,
): List<List<Position>> {
    val results = mutableListOf<List<Position>>()
    val queue = mutableListOf(listOf(start))

    while (queue.isNotEmpty()) {
        val path = queue.removeFirst()
        val current = path.last()

        if (current == end) {
            results.add(path)
            continue
        }
        neighbors(current)
            .filter { it !in path }
            .forEach { n -> queue.add(path + n) }
    }

    return results
}