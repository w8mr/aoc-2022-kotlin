package aoc2022

import aoc.*

class Day22() {
    enum class GridType(val text: String) {
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

    operator fun Dir.minus(other: Dir) =
        Dir.values()[(this.ordinal - other.ordinal).mod(Dir.values().size)]

    sealed interface Instruction {
        data class Rotate(val by: Int) : Instruction
        data class Forward(val steps: Int) : Instruction
    }

    val gridType = byEnum(GridType::class, GridType::text)
    val gridLine = oneOrMore(gridType).asArray() followedBy "\n"
    val gridParser = oneOrMore(gridLine).asArray() followedBy "\n"
    val instruction = oneOf(
        "L" asValue Instruction.Rotate(-1),
        "R" asValue Instruction.Rotate(1),
        number() map (Instruction::Forward)
    )
    val instructions = oneOrMore(instruction)
    val parser = seq(gridParser, instructions)

    data class State(val x: Int, val y: Int, val dir: Dir, val rot: Int) {
        val vdir get() = calculateChangedDir(rot)

        private fun calculateChangedDir(by: Int): Dir =
            Dir.values()[(dir.ordinal + by).mod(4)]

        fun changeDir(rotate: Instruction.Rotate) =
            copy(dir = calculateChangedDir(rotate.by))

        fun step(dir: Dir) =
            when (dir) {
                Dir.RIGHT -> copy(x = x + 1)
                Dir.DOWN -> copy(y = y + 1)
                Dir.LEFT -> copy(x = x - 1)
                Dir.UP -> copy(y = y - 1)
            }
    }


    fun solve(input: String, handleEmpty: (grid: Array<Array<GridType>>, oldState: State, newState: State) -> State): Int {
        val (grid, instructions) = parser.parse(input)
        var state = State(grid[0].indexOfFirst { it != GridType.EMPTY }, 0, Dir.RIGHT, 0)

        fun forward(
            instruction: Instruction.Forward) {
                (1..instruction.steps).forEach { step ->
                    var newState = state.step(state.vdir)
                    if (grid.cell(newState) == GridType.EMPTY) {
                        newState = handleEmpty(grid, state, newState)
                    }
                    when (grid.cell(newState)) {
                        GridType.OPEN -> state = newState
                        GridType.WALL -> return
                        GridType.EMPTY -> {}
                    }
                }
            }

        instructions.forEach { instruction ->
            when (instruction) {
                is Instruction.Rotate -> state = state.changeDir(instruction)
                is Instruction.Forward -> forward(instruction)
            }
        }
        return (state.y + 1) * 1000 + (state.x + 1) * 4 + state.vdir.ordinal
    }


    fun part1(input: String): Int {
        fun handleEmpty(grid: Array<Array<GridType>>, oldState: State, newState: State) =
            when (newState.vdir) {
                Dir.RIGHT -> newState.copy(x = grid[newState.y].indexOfFirst { it != GridType.EMPTY })
                Dir.DOWN -> newState.copy(y = grid.indexOfFirst { it.getOrElse(newState.x) { GridType.EMPTY } != GridType.EMPTY })
                Dir.LEFT -> newState.copy(x = grid[newState.y].size - 1)
                Dir.UP -> newState.copy(y = grid.indexOfLast { it.getOrElse(newState.x) { GridType.EMPTY } != GridType.EMPTY })
            }

        return solve(input, ::handleEmpty)
    }


    fun part2(input: String): Int {
        val isTest = input.length <200
        val cubeSize = if (isTest) 4 else 50
        val cubeMap = if (isTest) testCubeMap() else actualCubeMap()

        fun getRegion(state: State): Pair<Int, Int> =
            (state.y / cubeSize) to (state.x / cubeSize)

        fun getXYInRegion(region: Pair<Int, Int>, state:State) =
            Pair(
                state.x - region.second * cubeSize,
                state.y - region.first * cubeSize
            )



        fun handleEmpty(grid: Array<Array<GridType>>, oldState: State, newState: State): State {
            val region = getRegion(oldState)

            val (xInRegion, yInRegion) = getXYInRegion(region, oldState)
            val other = cubeMap.getValue(region).getValue(newState.vdir)

            val rotate = (newState.rot + other.second.ordinal + 2 - newState.vdir.ordinal).mod(Dir.values().size)
            val pos = when (newState.vdir) {
                Dir.UP -> xInRegion
                Dir.RIGHT -> yInRegion
                Dir.DOWN -> cubeSize - 1 - xInRegion
                Dir.LEFT -> cubeSize - 1 - yInRegion
            }
            val state = when (other.second) {
                Dir.UP -> newState.copy(
                    x = (other.first.second + 1) * cubeSize - 1 - pos,
                    y = other.first.first * cubeSize,
                    rot = rotate
                )

                Dir.RIGHT -> newState.copy(
                    x = (other.first.second + 1) * cubeSize -1,
                    y = (other.first.first + 1) * cubeSize - 1 - pos,
                    rot = rotate
                )

                Dir.DOWN -> newState.copy(
                    x = other.first.second * cubeSize + pos,
                    y = (other.first.first + 1) * cubeSize - 1,
                    rot = rotate
                )

                Dir.LEFT -> newState.copy(
                    x = other.first.second * cubeSize,
                    y = other.first.first * cubeSize + pos,
                    rot = rotate
                )

            }
            return state

        }

        return solve(input, ::handleEmpty)
    }

    private fun actualCubeMap(): Map<Pair<Int, Int>, Map<Dir, Pair<Pair<Int, Int>, Dir>>> {
        /*
            +C-++-D+
            |0 ||0 |
            A 1|| 2F
            +--++G-+
            +--+
            |1 G
            B 1|
            +--+
        +-B++--+
        A2 ||2 F
        | 0|| 1|
        +--++E-+
        +--+
        C3 E
        | 0|
        +-D+
        */

        val cubeMap = mapOf(
            Pair(0, 1) to mapOf(
                Dir.LEFT to Pair(Pair(2, 0), Dir.LEFT),  //A
                Dir.UP to Pair(Pair(3, 0), Dir.LEFT),  //C
            ),
            Pair(0, 2) to mapOf(
                Dir.UP to Pair(Pair(3, 0), Dir.DOWN),  //D
                Dir.RIGHT to Pair(Pair(2, 1), Dir.RIGHT),  //F
                Dir.DOWN to Pair(Pair(1, 1), Dir.RIGHT),  //G
            ),
            Pair(1, 1) to mapOf(
                Dir.LEFT to Pair(Pair(2, 0), Dir.UP),  //B
                Dir.RIGHT to Pair(Pair(0, 2), Dir.DOWN),  //G
            ),
            Pair(2, 0) to mapOf(
                Dir.LEFT to Pair(Pair(0, 1), Dir.LEFT),  //A
                Dir.UP to Pair(Pair(1, 1), Dir.LEFT),  //B
            ),
            Pair(2, 1) to mapOf(
                Dir.RIGHT to Pair(Pair(0, 2), Dir.RIGHT),  //F
                Dir.DOWN to Pair(Pair(3, 0), Dir.RIGHT),  //E
            ),
            Pair(3, 0) to mapOf(
                Dir.LEFT to Pair(Pair(0, 1), Dir.UP),  //C
                Dir.DOWN to Pair(Pair(0, 2), Dir.UP),  //D
                Dir.RIGHT to Pair(Pair(2, 1), Dir.DOWN),  //e
            ),
        )

        assert(cubeMap.all {
            it.value.all { inner ->
                cubeMap.getValue(inner.value.first).getValue(inner.value.second).first == it.key
            }
        })
        return cubeMap
    }

}


private fun testCubeMap(): Map<Pair<Int, Int>, Map<Day22.Dir, Pair<Pair<Int, Int>, Day22.Dir>>> {
    /*
            +B-+
            |0 C
            A 2|
            +--+
    +-B++-A++--+
    E1 ||1 ||1 |
    | 0|| 1|| 2D
    +-F++-G++--+
            +--++D-+
            G2 ||2 |
            | 2|| 3C
            +-F++-E+
    */

    val cubeMap = mapOf(
        Pair(0, 2) to mapOf(
            Day22.Dir.LEFT to Pair(Pair(1, 1), Day22.Dir.UP),  //A
            Day22.Dir.UP to Pair(Pair(1, 0), Day22.Dir.UP), //B
            Day22.Dir.RIGHT to Pair(Pair(2, 3), Day22.Dir.RIGHT), //C
        ),
        Pair(1, 0) to mapOf(
            Day22.Dir.UP to Pair(Pair(0, 2), Day22.Dir.UP), //B
            Day22.Dir.LEFT to Pair(Pair(2, 3), Day22.Dir.DOWN), //E
            Day22.Dir.DOWN to Pair(Pair(2, 2), Day22.Dir.DOWN), //F
        ),
        Pair(1, 1) to mapOf(
            Day22.Dir.UP to Pair(Pair(0, 2), Day22.Dir.LEFT),  //A
            Day22.Dir.DOWN to Pair(Pair(2, 2), Day22.Dir.LEFT), //G
        ),
        Pair(1, 2) to mapOf(
            Day22.Dir.RIGHT to Pair(Pair(2, 3), Day22.Dir.UP), //D
        ),
        Pair(2, 2) to mapOf(
            Day22.Dir.LEFT to Pair(Pair(1, 1), Day22.Dir.DOWN), //G
            Day22.Dir.DOWN to Pair(Pair(1, 0), Day22.Dir.DOWN), //F
        ),
        Pair(2, 3) to mapOf(
            Day22.Dir.UP to Pair(Pair(1, 2), Day22.Dir.RIGHT), //D
            Day22.Dir.RIGHT to Pair(Pair(0, 2), Day22.Dir.RIGHT), //C
            Day22.Dir.DOWN to Pair(Pair(1, 0), Day22.Dir.LEFT), //E
        ),
    )

    assert(cubeMap.all {
        it.value.all { inner ->
            cubeMap.getValue(inner.value.first).getValue(inner.value.second).first == it.key
        }
    })
    return cubeMap
}

fun Array<Array<Day22.GridType>>.cell(state: Day22.State) =
    this.getOrElse(state.y) { arrayOf() }.getOrElse(state.x) { Day22.GridType.EMPTY }

