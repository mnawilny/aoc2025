package com.nawilny.aoc2025.day11

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println

fun main() {
    val lines = Input.readFileLines("day11", "input.txt")
    val devices = lines.map { it.split(" ") }.map {
        val name = it.first().dropLast(1)
        Device(name, it.drop(1))
    }.associateBy { it.name }

    countPaths("you", devices, setOf()).println()
    countPaths("svr", devices, setOf()).println()
}

private data class NodeState(
    var pathsWithDACOnly: Long,
    var pathsWithFFTOnly: Long,
    var pathsWithBoth: Long,
    var pathsWithoutAny: Long
)

private val cache = mutableMapOf<String, NodeState>()

private fun countPaths(node: String, devices: Map<String, Device>, pathSoFar: Set<String>): NodeState {
    if (cache.contains(node)) {
        return cache[node]!!
    }
    if (node == "out") {
        val result = NodeState(0, 0, 0, 1)
        cache[node] = result
        return result
    }
    val device = devices[node]!!
    val newPath = pathSoFar.plus(node)
    val state = NodeState(0, 0, 0, 0)
    device.outputs.forEach {
        val s = countPaths(it, devices, newPath)
        when (node) {
            "fft" -> {
                state.pathsWithBoth += s.pathsWithBoth + s.pathsWithDACOnly
                state.pathsWithFFTOnly += s.pathsWithFFTOnly + s.pathsWithoutAny
            }

            "dac" -> {
                state.pathsWithBoth += s.pathsWithBoth + s.pathsWithFFTOnly
                state.pathsWithDACOnly += s.pathsWithDACOnly + s.pathsWithoutAny
            }

            else -> {
                state.pathsWithBoth += s.pathsWithBoth
                state.pathsWithDACOnly += s.pathsWithDACOnly
                state.pathsWithFFTOnly += s.pathsWithFFTOnly
                state.pathsWithoutAny += s.pathsWithoutAny
            }
        }
    }
    cache[node] = state
    return state
}

private data class Device(val name: String, val outputs: List<String>)
