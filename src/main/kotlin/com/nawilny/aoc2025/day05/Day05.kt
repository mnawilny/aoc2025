package com.nawilny.aoc2025.day05

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println

fun main() {
    val lines = Input.readFileLines("day05", "input.txt")
    val input = parseInput(lines)
    input.println()

    input.ids.count { id -> input.ranges.any { it.contains(id) } }.println()

    val combinedRanges = input.ranges.fold(listOf<LongRange>()) { acc, r -> combineRanges(acc, r) }
    input.ids.count { id -> combinedRanges.any { it.contains(id) } }.println()

    combinedRanges.sumOf { it.last - it.first + 1 }.println()
}

private fun combineRanges(existingRanges: List<LongRange>, newRange: LongRange): List<LongRange> {
    if (existingRanges.isEmpty()) {
        return listOf(newRange)
    }
    var newRangeMerged: LongRange? = newRange
    val toReplace = mutableSetOf<LongRange>()
    var finished = false
    while (!finished) {
        var processed = false
        for (existingRange in existingRanges) {
            if (existingRange.contains(newRangeMerged!!.first) && existingRange.contains(newRangeMerged.last)) {
                newRangeMerged = null
                finished = true
                processed = true
                break
            } else if (newRangeMerged.contains(existingRange.first) && newRangeMerged.contains(existingRange.last)) {
                toReplace.add(existingRange)
                finished = true
                processed = true
            } else if (existingRange.contains(newRangeMerged.first)) {
                newRangeMerged = LongRange(existingRange.first, newRangeMerged.last)
                toReplace.add(existingRange)
                processed = true
            } else if (existingRange.contains(newRangeMerged.last)) {
                newRangeMerged = LongRange(newRangeMerged.first, existingRange.last)
                toReplace.add(existingRange)
                processed = true
            }
        }
        if (!processed) {
            finished = true
        }
    }

    val newList = existingRanges.minus(toReplace)
    return if (newRangeMerged != null) newList.plusElement(newRangeMerged) else newList
}

private data class InputIds(val ranges: List<LongRange>, val ids: List<Long>)

private fun parseInput(lines: List<String>): InputIds {
    var remainingLines = lines
    val ranges = mutableListOf<LongRange>()
    while (remainingLines.first().isNotEmpty()) {
        val range = remainingLines.first().split("-")
        ranges.add(LongRange(range[0].toLong(), range[1].toLong()))
        remainingLines = remainingLines.drop(1)
    }
    remainingLines = remainingLines.drop(1)
    val ids = remainingLines.map { it.toLong() }
    return InputIds(ranges, ids)
}