package com.nawilny.aoc2025.day09

import com.nawilny.aoc2025.commons.Input
import com.nawilny.aoc2025.commons.Input.println
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    val points = Input.readFileLines("day09", "input.txt").map { it.split(",") }
        .map { Point(it[0].toLong(), it[1].toLong()) }

    var i = 0
    val xMap = points.map { it.x }.distinct().sorted().associateWith { i++ }
    i = 0
    val yMap = points.map { it.y }.distinct().sorted().associateWith { i++ }

    val greenTiles = mutableSetOf<Point>()
    val redTiles = mutableMapOf<Point, RedTile>()
    points.withIndex().forEach { tileWithIndex ->
        val tile = tileWithIndex.value
        val nextTile = points[if (tileWithIndex.index == points.size - 1) 0 else tileWithIndex.index + 1]
        val prevTile = points[if (tileWithIndex.index == 0) points.size - 1 else tileWithIndex.index - 1]

        val d1 = getDirection(tile, prevTile)
        val d2 = getDirection(tile, nextTile)
        val directions = mutableSetOf(d1, d2)
        val orientation = when {
            directions.contains(Direction.TOP) && directions.contains(Direction.BOTTOM) -> RedTileOrientation.VERTICAL
            directions.contains(Direction.LEFT) && directions.contains(Direction.RIGHT) -> RedTileOrientation.HORIZONTAL
            directions.contains(Direction.TOP) && directions.contains(Direction.LEFT) -> RedTileOrientation.TOP_LEFT
            directions.contains(Direction.TOP) && directions.contains(Direction.RIGHT) -> RedTileOrientation.TOP_RIGHT
            directions.contains(Direction.BOTTOM) && directions.contains(Direction.LEFT) -> RedTileOrientation.BOTTOM_LEFT
            directions.contains(Direction.BOTTOM) && directions.contains(Direction.RIGHT) -> RedTileOrientation.BOTTOM_RIGHT
            else -> error("Invalid state of directions $directions")
        }
        val newX = xMap[tile.x]!!
        val newY = yMap[tile.y]!!
        redTiles[Point(newX.toLong(), newY.toLong())] = RedTile(
            x = newX,
            y = newY,
            originalX = tile.x,
            originalY = tile.y,
            orientation = orientation
        )

        val nextX = xMap[nextTile.x]!!
        val nextY = yMap[nextTile.y]!!
        if (newX == nextX) {
            (min(newY, nextY) + 1..<max(newY, nextY))
                .forEach { greenTiles.add(Point(newX.toLong(), it.toLong())) }
        } else {
            (min(newX, nextX) + 1..<max(newX, nextX))
                .forEach { greenTiles.add(Point(it.toLong(), newY.toLong())) }
        }
    }

    var state = TilesAddingState.NOTHING
    var previousState = TilesAddingState.NOTHING
    var previousRedOrientation = RedTileOrientation.VERTICAL
    (redTiles.keys.minOf { it.y }..redTiles.keys.maxOf { it.y }).forEach { y ->
        (redTiles.keys.minOf { it.x }..redTiles.keys.maxOf { it.x }).forEach { x ->
            val tile = Point(x, y)
            when (state) {
                TilesAddingState.NOTHING -> {
                    if (redTiles.contains(tile)) {
                        val redTile = redTiles[tile]!!
                        when (redTile.orientation) {
                            RedTileOrientation.VERTICAL -> {
                                state = TilesAddingState.ADDING
                            }

                            RedTileOrientation.HORIZONTAL, RedTileOrientation.TOP_LEFT, RedTileOrientation.BOTTOM_LEFT -> {
                                error("Invalid state")
                            }

                            else -> {
                                previousState = state
                                state = TilesAddingState.BETWEEN_RED
                                previousRedOrientation = redTile.orientation
                            }
                        }
                    } else if (greenTiles.contains(tile)) {
                        state = TilesAddingState.ADDING
                    }
                }

                TilesAddingState.BETWEEN_RED -> {
                    if (redTiles.contains(tile)) {
                        val redTile = redTiles[tile]!!
                        when (redTile.orientation) {
                            RedTileOrientation.HORIZONTAL -> {
                                // ignore
                            }

                            RedTileOrientation.VERTICAL, RedTileOrientation.TOP_RIGHT, RedTileOrientation.BOTTOM_RIGHT -> {
                                error("Invalid state")
                            }

                            RedTileOrientation.BOTTOM_LEFT -> {
                                state = if (previousRedOrientation == RedTileOrientation.BOTTOM_RIGHT) {
                                    previousState
                                } else {
                                    if (previousState == TilesAddingState.ADDING) TilesAddingState.NOTHING else TilesAddingState.ADDING
                                }
                            }

                            RedTileOrientation.TOP_LEFT -> {
                                state = if (previousRedOrientation == RedTileOrientation.TOP_RIGHT) {
                                    previousState
                                } else {
                                    if (previousState == TilesAddingState.ADDING) TilesAddingState.NOTHING else TilesAddingState.ADDING
                                }
                            }
                        }
                    }
                }

                TilesAddingState.ADDING -> {
                    if (redTiles.contains(tile)) {
                        val redTile = redTiles[tile]!!
                        when (redTile.orientation) {
                            RedTileOrientation.VERTICAL -> {
                                state = TilesAddingState.ADDING
                            }

                            RedTileOrientation.HORIZONTAL, RedTileOrientation.TOP_LEFT, RedTileOrientation.BOTTOM_LEFT -> {
                                error("Invalid state")
                            }

                            else -> {
                                previousState = state
                                state = TilesAddingState.BETWEEN_RED
                                previousRedOrientation = redTile.orientation
                            }
                        }
                    } else if (greenTiles.contains(tile)) {
                        state = TilesAddingState.NOTHING
                    } else {
                        greenTiles.add(tile)
                    }
                }
            }
        }
    }

    printTiles(redTiles, greenTiles)

    val pairs = findPairs(redTiles.keys.toList())

    pairs.maxOf {
        val r1 = redTiles[it.first]!!
        val r2 = redTiles[it.second]!!
        (abs(r1.originalX - r2.originalX) + 1) * (abs(r1.originalY - r2.originalY) + 1)
    }.println()

    val redAndGreenTiles = greenTiles.plus(redTiles.keys)
    pairs.filter { containsOnlyRedOrGreen(it, redAndGreenTiles) }.maxOf {
        val r1 = redTiles[it.first]!!
        val r2 = redTiles[it.second]!!
        (abs(r1.originalX - r2.originalX) + 1) * (abs(r1.originalY - r2.originalY) + 1)
    }.println()
}

private fun containsOnlyRedOrGreen(pair: Pair<Point, Point>, redAndGreenTiles: Set<Point>): Boolean {
    (min(pair.first.y, pair.second.y)..max(pair.first.y, pair.second.y)).forEach { y ->
        (min(pair.first.x, pair.second.x)..max(pair.first.x, pair.second.x)).forEach { x ->
            val p = Point(x, y)
            if (!redAndGreenTiles.contains(p)) {
                return false
            }
        }
    }
    return true
}

private enum class TilesAddingState {
    NOTHING, BETWEEN_RED, ADDING
}

private fun printTiles(redTiles: Map<Point, RedTile>, greenTiles: Set<Point>) {
    (redTiles.keys.minOf { it.y }..redTiles.keys.maxOf { it.y }).forEach { y ->
        (redTiles.keys.minOf { it.x }..redTiles.keys.maxOf { it.x }).forEach { x ->
            val p = Point(x, y)
            if (redTiles.contains(p)) {
                print(
                    when (redTiles[p]!!.orientation) {
                        RedTileOrientation.BOTTOM_RIGHT -> "┌"
                        RedTileOrientation.BOTTOM_LEFT -> "┐"
                        RedTileOrientation.TOP_RIGHT -> "└"
                        RedTileOrientation.TOP_LEFT -> "┘"
                        RedTileOrientation.HORIZONTAL -> "─"
                        RedTileOrientation.VERTICAL -> "│"
                    }
                )
            } else if (greenTiles.contains(p)) {
                print("X")
            } else {
                print(".")
            }
        }
        println()
    }
}


private data class Point(val x: Long, val y: Long)

private data class RedTile(
    val x: Int,
    val y: Int,
    val originalX: Long,
    val originalY: Long,
    val orientation: RedTileOrientation
)

private enum class Direction {
    TOP, LEFT, RIGHT, BOTTOM
}

private fun getDirection(p1: Point, p2: Point): Direction {
    return if (p1.x == p2.x) {
        if (p1.y < p2.y) Direction.BOTTOM else Direction.TOP
    } else {
        if (p1.x < p2.x) Direction.RIGHT else Direction.LEFT
    }
}

private enum class RedTileOrientation {
    HORIZONTAL, VERTICAL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

private fun <T> findPairs(elements: List<T>): List<Pair<T, T>> {
    return (0..elements.size - 2).flatMap { i1 ->
        (i1 + 1..<elements.size).map { Pair(elements[i1], elements[it]) }
    }
}