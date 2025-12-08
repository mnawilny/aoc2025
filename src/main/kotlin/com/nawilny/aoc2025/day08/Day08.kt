package com.nawilny.aoc2025.day08

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println
import kotlin.math.abs
import kotlin.math.sqrt

fun main() {
    val boxes = Input.readFileLines("day08", "input.txt").map { it.split(",") }
        .map { Box(it[0].toInt(), it[1].toInt(), it[2].toInt()) }

    val distances = (0..boxes.size - 2).flatMap { b1Index ->
        (b1Index + 1..<boxes.size).map { b2Index ->
            val b1 = boxes[b1Index]
            val b2 = boxes[b2Index]
            val dx = abs(b1.x - b2.x).toDouble()
            val dy = abs(b1.y - b2.y).toDouble()
            val dz = abs(b1.z - b2.z).toDouble()
            val d = sqrt((dx * dx) + (dy * dy) + (dz * dz))
            Distance(b1, b2, d)
        }
    }.sortedBy { it.distance }

    val circuits = boxes.map { Circuit(mutableSetOf(it)) }.toMutableList()

    var distanceIndex = 0
    while (distanceIndex < 1000) {
        connect(distances, distanceIndex++, circuits)
    }
    circuits.map { it.boxes.size }.sortedDescending().take(3).fold(1) { acc, i -> acc * i }.println()

    while (circuits.size > 1) {
        connect(distances, distanceIndex++, circuits)
    }
    val lastDistance = distances[distanceIndex - 1]
    (lastDistance.b1.x.toLong() * lastDistance.b2.x).println()
}

private fun connect(distances: List<Distance>, distanceIndex: Int, circuits: MutableList<Circuit>) {
    val d = distances[distanceIndex]
    val c1 = circuits.find { it.boxes.contains(d.b1) }!!
    val c2 = circuits.find { it.boxes.contains(d.b2) }!!
    if (c1 != c2) {
        circuits.remove(c2)
        c1.boxes.addAll(c2.boxes)
    }
}

private data class Box(val x: Int, val y: Int, val z: Int)

private data class Distance(val b1: Box, val b2: Box, val distance: Double)

private data class Circuit(val boxes: MutableSet<Box>)
