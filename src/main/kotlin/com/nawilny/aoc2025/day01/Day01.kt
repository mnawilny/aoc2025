package com.nawilny.aoc2025.day01

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println

fun main() {
    val lines = Input.readFileLines("day01", "example.txt")

    var currentState = State(position = 50, zerosEndPosition = 0, zerosClick = 0)
    lines.forEach {
        currentState = rotateNaive(currentState, it)
    }
    currentState.println()
}

private fun rotateNaive(state: State, move: String): State {
    val direction = move.first()
    val distance = move.drop(1).toInt()
    var zerosClick = state.zerosClick
    var zerosEndPosition = state.zerosEndPosition
    var position = state.position

    for (i in 0..<distance) {
        position = when (direction) {
            'R' -> position + 1
            'L' -> position - 1
            else -> error("Unsupported direction $direction")
        }
        if (position == 100) {
            position = 0
        }
        if (position == -1) {
            position = 99
        }
        if (position == 0) {
            zerosClick++
        }
    }
    if (position == 0) {
        zerosEndPosition++
    }
    return State(position, zerosEndPosition, zerosClick)
}

private data class State(val position: Int, val zerosEndPosition: Int, val zerosClick: Int)