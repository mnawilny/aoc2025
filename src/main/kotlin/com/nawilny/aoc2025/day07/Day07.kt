package com.nawilny.aoc2025.day07

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println

fun main() {
    val lines = Input.readFileLines("day07", "input.txt")
    val splitters = lines.map { line ->
        line.withIndex().filter { it.value == '^' }.map { it.index }.toSet()
    }.filter { it.isNotEmpty() }
    val start = lines.first().indexOf('S')
    val lineSize = splitters.maxOf { it.max() } + 2

    var possibleTimelines = List(lineSize) { if (it == start) 1L else 0L }
    var splitterCount = 0

    splitters.forEach { splittersLine ->
        val newPossibleTimelines = MutableList(lineSize) { 0L }
        possibleTimelines.withIndex().filter { it.value > 0 }.forEach {
            if (splittersLine.contains(it.index)) {
                newPossibleTimelines[it.index-1] += it.value
                newPossibleTimelines[it.index+1] += it.value
                splitterCount++
            } else {
                newPossibleTimelines[it.index] += it.value
            }
        }
        possibleTimelines = newPossibleTimelines
    }
    splitterCount.println()
    possibleTimelines.sum().println()
}
