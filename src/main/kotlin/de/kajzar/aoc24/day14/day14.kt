package de.kajzar.aoc24.day14

import de.kajzar.aoc24.Day

private fun main() {
    val machines = Day(14)
        .input()
        .readLines()
        .map { l ->
            val (p, v) = l.split(" ")
            Robot(p.extract(), v.extract())
        }

    val world = World(0 until 101, 0 until 103)

    // part 1
    world.progression(machines)
        // simulate 100 seconds
        .take(100 + 1)
        .last()
        // group into quadrants
        .groupBy { it.quadrant(world) }
        .filterKeys { it != null }
        // multiply values
        .values
        .fold(1L) { acc, robots -> acc * robots.count() }
        .also(::println)

    // part 2
    world.progression(machines)
        .indexOfFirst { robots ->
            // tree has many robots in a row
            val byRow = robots.map { it.position }
                .groupBy { it.y() }
            byRow.any { (_, entries) ->
                entries.map { it.x() }
                    .sorted()
                    .countConsecutive() > 10
            }
        }
        .also { println(it) }
}

private data class World(val x: IntRange, val y: IntRange)

private typealias Position = Pair<Int, Int>

private fun Position.x() = first
private fun Position.y() = second

private data class Robot(val position: Position, val velocity: Velocity)

private typealias Velocity = Pair<Int, Int>

private fun Velocity.dx() = first
private fun Velocity.dy() = second

private fun Position.plus(v: Velocity) = Position(x() + v.dx(), y() + v.dy())

private fun String.extract(): Pair<Int, Int> = substringAfter("=")
    .split(",")
    .map { it.toInt() }
    .let { (a, b) -> a to b }

private fun World.progression(robots: List<Robot>) = generateSequence(robots) { list ->
    list.map { it.progress(this) }
}

private fun List<Int>.countConsecutive(): Int {
    var last: Int? = null
    var count = 0
    var max = 0
    forEach { c ->
        if (c == last?.plus(1)) {
            count++
            last = c
        } else {
            max = maxOf(max, count)
            count = 1
            last = c
        }
    }
    return maxOf(max, count)
}

private fun Robot.quadrant(world: World): Int? {
    val midX = world.x.count() / 2
    val midY = world.y.count() / 2

    return when {
        position.x() < midX && position.y() < midY -> 1
        position.x() > midX && position.y() < midY -> 2
        position.x() < midX && position.y() > midY -> 3
        position.x() > midX && position.y() > midY -> 4
        else -> null
    }
}

private fun Robot.progress(world: World): Robot {
    var (nx, ny) = position.plus(velocity)

    // adjust out of bounds
    if (nx !in world.x) {
        if (nx < 0) {
            nx += world.x.count()
        } else {
            nx -= world.x.count()
        }
    }

    if (ny !in world.y) {
        if (ny < 0) {
            ny += world.y.count()
        } else {
            ny -= world.y.count()
        }
    }

    return copy(position = Position(nx, ny))
}