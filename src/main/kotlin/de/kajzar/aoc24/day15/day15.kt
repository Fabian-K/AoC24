package de.kajzar.aoc24.day15

import de.kajzar.aoc24.Day
import de.kajzar.aoc24.split

private fun main() {
    // part 1
    readMap().let { (map, sequence) ->
        map.simulate(sequence)
        map.findAll { it == 'O' }
            .sumOf { it.y() * 100 + it.x() }
            .let { println(it) }
    }

    // part 2
    readMap().let { (map, sequence) ->
        val expanded = map.expand()
        expanded.simulate(sequence)
        expanded.findAll { it == '[' }
            .sumOf { it.y() * 100 + it.x() }
            .let { println(it) }
    }
}

private fun Map.simulate(sequence: List<Char>) {
    sequence.fold(this) { m, c -> m.apply { moveRobot(c) } }
}

private typealias Map = List<MutableList<Char>>
private typealias Position = Pair<Int, Int>

private fun Position.x() = first
private fun Position.y() = second

private fun Map.get(pos: Position) = get(pos.y()).get(pos.x())

private fun readMap(): Pair<Map, List<Char>> = Day(15)
    .input()
    .readLines()
    .split { it.isEmpty() }
    .let { (m, s) ->
        val map = m.map { it.toCharArray().toMutableList() }
        val seq = s.joinToString(separator = "").toCharArray().toList()
        map to seq
    }

private fun Map.moveRobot(direction: Char) {
    val robotPos = findAll { it == '@' }.single()

    // find affected positions
    val affected = mutableSetOf<Position>()
    val queue = mutableListOf(robotPos)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        affected.add(current)
        val currentChar = get(current)

        // if wall -> nothing can move
        if (currentChar == '#') return

        // if free -> nothing else is affected
        if (currentChar in setOf('.')) continue

        if (currentChar == '@') {
            // affects in direction of movement
            current.next(direction).let { if (it !in affected) queue.add(it) }
            continue
        }

        // affects nightbor to right
        if (currentChar == '[')
            current.next('>').let { if (it !in affected) queue.add(it) }

        // affects nightbor to left
        if (currentChar == ']')
            current.next('<').let { if (it !in affected) queue.add(it) }

        current.next(direction).let { if (it !in affected) queue.add(it) }
    }

    // otherwise move all positions
    val updates = affected
        .map { p -> p.next(direction) to get(p) }
        // "free don´t propagate
        .filter { (_, c) -> c != '.' }

    // apply updates
    updates.forEach { (pos, value) ->
        this[pos.y()][pos.x()] = value
    }

    // clear affected positions that have not been updated
    affected.minus(updates.map { (p, _) -> p }.toSet()).forEach {
        this[it.y()][it.x()] = '.'
    }
}

private fun Position.next(direction: Char): Position {
    return when (direction) {
        '<' -> x() - 1 to y()
        '>' -> x() + 1 to y()
        '^' -> x() to y() - 1
        'v' -> x() to y() + 1
        else -> error("Invalid direction: $direction")
    }
}

private fun Map.expand(): Map = map {
    it.flatMap { c ->
        when (c) {
            '#' -> listOf('#', '#')
            '.' -> listOf('.', '.')
            '@' -> listOf('@', '.')
            'O' -> listOf('[', ']')
            else -> error("Invalid char: $c")
        }
    }.toMutableList()
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