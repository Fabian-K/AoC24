package de.kajzar.aoc24.day8

import de.kajzar.aoc24.Day

fun main() {
    val map: Map = Day(8)
        .input()
        .readLines()
        .map { it.toCharArray().toList() }

    val antennas = map.findAll { it != '.' }
    val antennaGroups = antennas.groupBy { it.first }

    // part 1
    antennaGroups.generatePairs()
        .flatMap { it.antinodes() }
        .filter { map.contains(it) }
        .distinct()
        .count()
        .also { println(it) }

    // part 2
    antennaGroups.generatePairs()
        .flatMap { it.antinodes(map) }
        .distinct()
        .count()
        .also { println(it) }
}

private fun kotlin.collections.Map<Char, List<Point>>.generatePairs() = this
    .flatMap { (_, positions) -> positions.combinations() }
    .map { (a, b) -> AntennaPair(a.second, b.second) }

private fun <T> List<T>.combinations(): Set<Pair<T, T>> {
    val result = mutableSetOf<Pair<T, T>>()

    forEach { a ->
        forEach { b ->
            if (a != b) {
                if (result.contains(b to a)) return@forEach
                result.add(a to b)
            }
        }
    }

    return result
}

private data class AntennaPair(
    val a: Position,
    val b: Position,
)

private fun AntennaPair.antinodes(map: Map): List<Position> {
    val dx = b.x() - a.x()
    val dy = b.y() - a.y()

    val result = mutableSetOf(a)

    var nextX = a.move(dx, dy)
    while (map.contains(nextX)) {
        result.add(nextX)
        nextX = nextX.move(dx, dy)
    }

    var nextB = a.move(-dx, -dy)
    while (map.contains(nextB)) {
        result.add(nextB)
        nextB = nextB.move(-dx, -dy)
    }

    return result.toList()
}

private fun AntennaPair.antinodes(): List<Position> {
    val dx = b.x() - a.x()
    val dy = b.y() - a.y()

    return setOf(
        a.move(dx, dy),
        a.move(-dx, -dy),
        b.move(dx, dy),
        b.move(-dx, -dy),
    )
        .minus(a)
        .minus(b)
        .toList()
}

private fun Map.contains(position: Position) =
    position.y() in this.indices
            && position.x() in this[position.y()].indices

private fun Pair<Int, Int>.x() = this.first
private fun Pair<Int, Int>.y() = this.second

private typealias Map = List<List<Char>>
private typealias Position = Pair<Int, Int>
private typealias Point = Pair<Char, Position>

private fun Position.move(dx: Int, dy: Int) = x() + dx to y() + dy

private fun Map.findAll(test: (Char) -> Boolean): List<Point> {
    val result = mutableListOf<Point>()
    for (y in this.indices) {
        for (x in this[y].indices) {
            if (test(this[y][x])) {
                result.add(this[y][x] to (x to y))
            }
        }
    }
    return result
}