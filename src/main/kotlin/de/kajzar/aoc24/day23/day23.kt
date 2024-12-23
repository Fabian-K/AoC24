package de.kajzar.aoc24.day23

import de.kajzar.aoc24.Day


private fun main() {
    val connections = Day(23)
        .input()
        .readLines()
        .map { it.split("-") }

    val computers = connections.flatten().toSet()
    val neighbors = computers.associateWith {
        connections.filter { c -> c.contains(it) }
            .flatten()
            .toSet()
            .minus(it)
    }

    // part 1
    computers.filter { it.startsWith("t") }
        .flatMap { c ->
            neighbors.getValue(c).pairs()
                .filter { (a, b) -> neighbors.getValue(a).contains(b) }
                .map { (a, b) -> setOf(a, b, c) }
        }
        .distinct()
        .count()
        .also { println(it) }

    // part 2
    val cliques = findCliques(r = emptySet(), p = computers, x = emptySet()) { n -> neighbors.getValue(n) }
    cliques.maxBy { it.count() }
        .toList().sorted().joinToString(separator = ",")
        .also { println(it) }
}

private fun Set<String>.pairs(): List<Pair<String, String>> = buildList {
    this@pairs.forEach { a ->
        this@pairs.forEach { b ->
            if (a != b) add(a to b)
        }
    }
}

// TIL: Bron-Kerbosch algorithm
private fun findCliques(
    r: Set<String>,
    p: Set<String>,
    x: Set<String>,
    neighbors: (String) -> Set<String>,
): List<Set<String>> {
    if (p.isEmpty() && x.isEmpty())
        return listOf(r)

    val pivot = p.plus(x).random()
    return p.minus(neighbors(pivot))
        .flatMap { v ->
            val n = neighbors(v)
            findCliques(
                r = r + v,
                p = p.intersect(n),
                x = x.intersect(n),
                neighbors = neighbors
            )
        }
}