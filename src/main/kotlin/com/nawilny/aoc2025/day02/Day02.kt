package com.nawilny.aoc2025.day02

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println

fun main() {
    val ids = Input.readFileLines("day02", "input.txt").first().split(",")

    // part 1
    ids.map { it.split("-") }
        .map { LongRange(it[0].toLong(), it[1].toLong()) }
        .flatMap { it.toList() }
        .filter { !isValidIdDividedToParts(it.toString(), 2) }
        .sumOf { it }.println()

    // part 2
    ids.map { it.split("-") }
        .map { LongRange(it[0].toLong(), it[1].toLong()) }
        .flatMap { it.toList() }
        .filter { !isValidId(it.toString()) }
        .sumOf { it }.println()
}

private fun isValidId(id: String): Boolean {
    return (2..id.length).all { isValidIdDividedToParts(id, it) }
}

private fun isValidIdDividedToParts(id: String, parts: Int): Boolean {
    if (id.length % parts != 0) {
        return true
    }
    val pattern = id.substring(0, id.length / parts)
    var text = id
    while (text.isNotEmpty()) {
        val part = text.take(pattern.length)
        if (part != pattern) {
            return true
        }
        text = text.drop(pattern.length)
    }
    return false
}
