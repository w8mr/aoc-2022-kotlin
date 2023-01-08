package aoc2022

import aoc.*

class Day22() {
    enum class GridType(val text: String){
        EMPTY(" "),
        OPEN("."),
        WALL("#")
    }

    enum class Dir {
        RIGHT,
        DOWN,
        LEFT,
        UP
    }
    sealed interface Instruction {
        object Left: Instruction
        object Right: Instruction
        data class Forward(val steps: Int) : Instruction
    }

    val gridType = byEnum(GridType::class, GridType::text)
    val gridLine = oneOrMore(gridType).asArray() followedBy "\n"
    val grid = oneOrMore(gridLine).asArray() followedBy "\n"
    val instruction = oneOf(
        "L" asValue Instruction.Left,
        "R" asValue Instruction.Right,
        number() map(Instruction::Forward)
    )
    val instructions = oneOrMore(instruction)
    val parser = seq(grid, instructions)

    fun part1(input: String): Int {
        val (grid, instructions) = parser.parse(input)

        var x = grid[0].indexOfFirst { it != GridType.EMPTY }
        var y = 0
        var dir = Dir.RIGHT

        instructions.forEach { instruction ->
            when (instruction) {
                is Instruction.Left -> dir = Dir.values()[(dir.ordinal - 1).mod(4)]
                is Instruction.Right -> dir = Dir.values()[(dir.ordinal + 1).mod(4)]
                is Instruction.Forward -> {
                    run loop@ {
                        (1..instruction.steps).forEach { step ->
                            var (newX, newY) = when(dir) {
                                Dir.RIGHT -> x+1 to y
                                Dir.DOWN -> x to y+1
                                Dir.LEFT -> x-1 to y
                                Dir.UP -> x to y-1
                            }
                            var cell = grid.getOrElse(newY) { arrayOf() }.getOrElse(newX) { GridType.EMPTY }
                            if (cell == GridType.EMPTY) {
                                when (dir) {
                                    Dir.RIGHT -> newX = grid[newY].indexOfFirst { it != GridType.EMPTY }
                                    Dir.DOWN -> newY = grid.indexOfFirst { it.getOrElse(newX) { GridType.EMPTY } != GridType.EMPTY }
                                    Dir.LEFT -> newX = grid[newY].size-1
                                    Dir.UP -> newY = grid.indexOfLast { it.getOrElse(newX) { GridType.EMPTY } != GridType.EMPTY }
                                }
                                cell = grid.getOrElse(newY) { arrayOf() }.getOrElse(newX) { GridType.EMPTY }

                            }
                            when (cell) {
                                GridType.OPEN -> {
                                    x = newX
                                    y = newY
                                }

                                GridType.WALL -> { return@loop }
                                GridType.EMPTY -> { }
                            }
                        }
                    }
                }
            }
        }

        return (y+1)*1000+(x+1)*4+dir.ordinal
    }

    fun part2(input: String): Int {
        return TODO()
    }
}

