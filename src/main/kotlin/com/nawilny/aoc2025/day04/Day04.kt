package com.nawilny.aoc2025.day04

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println

fun main() {
    val lines = Input.readFileLines("day04", "input.txt")

    val rolls = lines.withIndex().flatMap { line ->
        line.value.withIndex().filter { it.value == '@' }.map { Position(line.index, it.index) }
    }.toSet()

    rollsToBeRemoved(rolls).count().println()

    var totalCounter = 0
    val currentRolls = rolls.toMutableSet()
    var toBeRemoved = rollsToBeRemoved(currentRolls)
    while (toBeRemoved.isNotEmpty()) {
        totalCounter += toBeRemoved.size
        currentRolls.removeAll(toBeRemoved)
        toBeRemoved = rollsToBeRemoved(currentRolls)
    }
    totalCounter.println()
}

private data class Position(val x: Int, val y: Int)

private fun rollsToBeRemoved(rolls: Set<Position>) =
    rolls.filter { roll -> getNeighbours(roll).count { rolls.contains(it) } < 4 }.toSet()

private fun getNeighbours(p: Position) = setOf(
    Position(p.x - 1, p.y - 1),
    Position(p.x - 1, p.y),
    Position(p.x - 1, p.y + 1),
    Position(p.x, p.y - 1),
    Position(p.x, p.y + 1),
    Position(p.x + 1, p.y - 1),
    Position(p.x + 1, p.y),
    Position(p.x + 1, p.y + 1)
)

