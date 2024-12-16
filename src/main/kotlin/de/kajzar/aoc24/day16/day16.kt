package de.kajzar.aoc24.day16

import de.kajzar.aoc24.Day

private fun main() {
    val map: Map = Day(16)
        .input()
        .readLines()
        .map { it.toCharArray().toList() }

    val start = map.findAll { it == 'S' }.single()
    val end = map.findAll { it == 'E' }.single()

    val result = dijkstra(
        nodes = map.findAll { it in setOf('S', '.', 'E') }
            .flatMap { p -> Direction.entries.map { it to p } },
        edges = { node ->
            map.neighborsOf(node)
        },
        start = Direction.E to start,
    )

    // part 1
    result.minDistanceToAny { node -> node.second == end }
        .also(::println)

    // part 2
    result
        .pathsTo { node -> node.second == end }
        .flatMap { path -> path.map { (_, pos) -> pos } }
        .toSet()
        .count()
        .also(::println)
}

private fun <N> dijkstra(nodes: List<N>, start: N, edges: (N) -> Set<Pair<N, Int>>): PathingResult<N> {

    val distance = nodes.associateWith { Int.MAX_VALUE }.toMutableMap()
    val prev = nodes.associateWith { emptySet<N>() }.toMutableMap()

    val queue = nodes.toMutableList()
    distance[start] = 0

    while (queue.isNotEmpty()) {
        val node = queue.minBy { node -> distance.getValue(node) }
        val d = distance.getValue(node)

        queue.remove(node)

        edges(node)
            .forEach { (neighbor, cost) ->
                val oldCost = distance.getValue(neighbor)
                val newCost = cost + d

                val prevForNeighbor = prev.getValue(neighbor)

                if (newCost == oldCost) {
                    distance[neighbor] = newCost
                    prev[neighbor] = prevForNeighbor.plus(node)
                } else if (newCost < oldCost) {
                    distance[neighbor] = newCost
                    prev[neighbor] = setOf(node)
                }
            }

    }
    return PathingResult(start, distance, prev)
}

private data class PathingResult<N>(
    val start: N,
    val distance: kotlin.collections.Map<N, Int>,
    val prev: kotlin.collections.Map<N, Set<N>>,
)

private fun <N> PathingResult<N>.pathsTo(pred: (N) -> Boolean): List<List<N>> {
    val shortest = distance.filterKeys(pred)
        .minOf { (_, d) -> d }

    val endNodes = distance.filterKeys(pred)
        .filterValues { it == shortest }

    return endNodes.keys
        .flatMap { end -> resolvePath(prev, start, end) }
}

private fun <N> resolvePath(previous: kotlin.collections.Map<N, Set<N>>, start: N, end: N): List<List<N>> {
    val queue = mutableListOf(listOf(end))
    val result = mutableListOf<List<N>>()
    while (queue.isNotEmpty()) {
        val next = queue.removeLast()
        if (next.last() == start) {
            result.add(next)
            continue
        }

        previous.getValue(next.last())
            .forEach { n -> queue.add(next + n) }
    }
    return result
}

private fun <N> PathingResult<N>.minDistanceToAny(pred: (N) -> Boolean): Int {
    return distance.filterKeys(pred)
        .minOf { (_, cost) -> cost }
}

private fun Map.neighborsOf(node: Node): Set<Pair<Node, Cost>> = buildSet {
    val (dir, pos) = node

    // move forward if possible
    val nextPos: Position = pos.next(dir)
    val char = get(nextPos)
    if (char != '#') add((dir to nextPos) to 1)

    // rotate
    add((dir.clockwise() to pos) to 1000)
    add((dir.counterClockwise() to pos) to 1000)
}


private typealias Cost = Int
private typealias Node = Pair<Direction, Position>

private typealias Map = List<List<Char>>
private typealias Position = Pair<Int, Int>

private fun Position.x() = first
private fun Position.y() = second

private fun Map.get(pos: Position) = get(pos.y()).get(pos.x())

private enum class Direction { N, E, S, W }

private fun Direction.clockwise(): Direction = when (this) {
    Direction.N -> Direction.E
    Direction.E -> Direction.S
    Direction.S -> Direction.W
    Direction.W -> Direction.N
}

private fun Direction.counterClockwise(): Direction = when (this) {
    Direction.N -> Direction.W
    Direction.W -> Direction.S
    Direction.S -> Direction.E
    Direction.E -> Direction.N
}

private fun Position.next(dir: Direction): Position = when (dir) {
    Direction.N -> x() to y() - 1
    Direction.E -> x() + 1 to y()
    Direction.S -> x() to y() + 1
    Direction.W -> x() - 1 to y()
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