package com.nawilny.aoc2025.day06

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println

fun main() {
    val lines = Input.readFileLines("day06", "input.txt")
    val input = parseInput(lines)
    solve(input) { basicNumbersParser(it) }.println()
    solve(input) { rightToLeftNumbersParser(it) }.println()
}

private fun solve(input: List<List<String>>, numbersParser: (List<String>) -> List<Long>): Long {
    return input.sumOf { args ->
        val operator = args.last().trim()
        val parsedArgs = numbersParser(args.dropLast(1))
        when (operator) {
            "+" -> parsedArgs.sum()
            "*" -> parsedArgs.fold(1L) { acc, i -> acc * i }
            else -> error("Unknown operator $operator")
        }
    }
}

private fun basicNumbersParser(numbers: List<String>) = numbers.map { it.trim().toLong() }

private fun rightToLeftNumbersParser(numbers: List<String>): List<Long> {
    var remaining = numbers
    val result = mutableListOf<Long>()
    while (remaining.isNotEmpty()) {
        val lastChars = remaining.map { it.last() }.filter { it != ' ' }.map { it.digitToInt() }
        remaining = remaining.map { it.dropLast(1) }.filter { it.isNotEmpty() }
        if (lastChars.isNotEmpty()) {
            result.add(lastChars.fold(0) { acc, i -> acc * 10 + i })
        }
    }
    return result
}

private fun parseInput(lines: List<String>): List<List<String>> {
    val max = lines.maxOf { it.length }
    val operatorsLine = lines.last()
    var current = List(lines.size - 1) { "" }
    var currentOperator = ' '
    val result = mutableListOf<List<String>>()
    (0..<max).forEach { index ->
        val c = getOrEmpty(operatorsLine, index)
        if (c != ' ') {
            if (currentOperator != ' ') {
                result.add(current.plusElement(currentOperator.toString()))
                current = List(lines.size - 1) { "" }
            }
            currentOperator = c
        }
        current = current.withIndex().map { it.value + getOrEmpty(lines[it.index], index) }
    }
    result.add(current.plusElement(currentOperator.toString()))
    return result
}

private fun getOrEmpty(line: String, i: Int) = if (i >= line.length) ' ' else line[i]
