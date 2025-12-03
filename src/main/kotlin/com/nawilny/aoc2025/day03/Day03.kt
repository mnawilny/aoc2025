package com.nawilny.aoc2025.day03

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println

fun main() {
    val lines = Input.readFileLines("day03", "input.txt")
    val batteries = lines.map { s -> s.map { it.digitToInt() } }
    batteries.sumOf { getLargestJoltageByTurningOn(it, 2) }.println()
    batteries.sumOf { getLargestJoltageByTurningOn(it, 12) }.println()
}

private fun getLargestJoltageByTurningOn(batteries: List<Int>, batteriesCount: Int): Long {
    if (batteriesCount == 1) {
        return batteries.max().toLong()
    }
    val max = batteries.dropLast(batteriesCount - 1).max()
    val position = batteries.indexOf(max)
    val remaining = batteries.drop(position + 1)
    return (max * pow10(batteriesCount - 1)) + getLargestJoltageByTurningOn(remaining, batteriesCount - 1)
}

private fun pow10(n: Int): Long {
    var i = 10L
    (1..<n).forEach { i *= 10 }
    return i
}
