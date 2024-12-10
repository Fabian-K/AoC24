package de.kajzar.aoc24.day9

import de.kajzar.aoc24.Day
import kotlin.streams.toList

fun main() {
    val input = Day(9)
        .input()
        .readLines()
        .single()
        .chars()
        .map { it.toChar().digitToInt() }
        .toList()

    var nextFileId = 0
    val entries = input
        .mapIndexed { index, i ->
            if (index % 2 == 0) {
                Entry.File(nextFileId++, i)
            } else {
                Entry.Free(i)
            }
        }
        .filter { !it.isEmpty() }

    // part 1
    entries.compact()
        .checksum()
        .also { println(it) }

    // part 2
    entries.compactKeepGroups()
        .checksum()
        .also { println(it) }
}

private fun List<Entry>.compactKeepGroups(): MutableList<Entry> {
    val result = toMutableList()

    val moveOrder = filterIsInstance<Entry.File>()
        .reversed()

    moveOrder.forEach { file ->
        val currentPos = result.indexOf(file)

        val firstSpace: Entry.Free? = result
            .firstOrNull { it is Entry.Free && it.size >= file.size } as Entry.Free?
        val firstSpacePos = firstSpace?.let { result.indexOf(it) }

        if (firstSpacePos != null && firstSpacePos < currentPos) {
            // move file to first space
            result[currentPos] = Entry.Free(file.size)
            result[firstSpacePos] = file

            val remaining = firstSpace.size - file.size
            if (remaining > 0)
                result.add(firstSpacePos + 1, Entry.Free(remaining))

            // compact "free"
            result.mergeAdjacent()
        }
    }
    return result
}

private fun List<Entry>.compact(): List<Entry> {
    val exploded = flatMap { it.explode() }
        .toMutableList()

    val rebuild = mutableListOf<Entry>()

    // compact exploded
    while (exploded.isNotEmpty()) {
        val next = exploded.removeFirst()
        when (next) {
            is Entry.File -> rebuild.add(next)
            is Entry.Free -> {
                while (exploded.isNotEmpty()) {
                    // remove from end ignoring free
                    when (val last = exploded.removeLast()) {
                        is Entry.File -> {
                            rebuild.add(last)
                            break
                        }

                        is Entry.Free -> {}
                    }
                }
            }
        }
    }
    return rebuild
}


private fun MutableList<Entry>.mergeAdjacent() {
    while (firstDoubleFreeIndex() != null) {
        val firstDoubleFreeIndex = firstDoubleFreeIndex()!!

        val a = get(firstDoubleFreeIndex) as Entry.Free
        val b = get(firstDoubleFreeIndex + 1) as Entry.Free

        removeAt(firstDoubleFreeIndex + 1)
        set(firstDoubleFreeIndex, Entry.Free(a.size + b.size))
    }
}

private sealed class Entry(val size: Int) {
    class File(val id: Int, size: Int) : Entry(size)
    class Free(size: Int) : Entry(size)

    fun isEmpty() = size == 0

    fun newInstance(size: Int) = when (this) {
        is File -> File(id, size)
        is Free -> Free(size)
    }

    fun explode(): List<Entry> {
        return if (size == 1)
            listOf(this)
        else
            List(size) { this.newInstance(1) }
    }

    fun checksum(index: Int) = when (this) {
        is File -> id.toLong() * index
        is Free -> 0L
    }
}

private fun List<Entry>.firstDoubleFreeIndex(): Int? {
    forEachIndexed { index, entry ->
        if (entry is Entry.Free && getOrNull(index + 1) is Entry.Free) {
            return index
        }
    }
    return null
}

private fun List<Entry>.checksum(): Long = this
    .flatMap { it.explode() }
    .mapIndexed { index, entry -> entry.checksum(index) }
    .sum()