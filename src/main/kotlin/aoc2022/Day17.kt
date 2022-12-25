package aoc2022

import aoc.*

class Day17() {

    enum class Move(val changeX: Int) {
        LEFT(-1),
        RIGHT(1)
    }

    enum class Block(val width:Int, val indents: List<Pair<Int, Int>>) {
        MINUS(4, listOf(0 to 0)),
        PLUS(3, listOf(1 to 1, 0 to 0, 1 to 1)),
        CORNER (3, listOf(0 to 0, 2 to 0, 2 to 0)),
        LINE(1, listOf(0 to 0, 0 to 0, 0 to 0, 0 to 0 )),
        DOT(2, listOf(0 to 0, 0 to 0))
    }

    val left = "<" asValue Move.LEFT
    val right = ">" asValue Move.RIGHT
    val parser = zeroOrMore(left or right) followedBy "\n"

    enum class CellState(val ch: Char){
        EMPTY(' '),
        FIXED('#'),
        MOVING('@')
    }

    data class State(val lines: MutableList<MutableList<CellState>> = makeMutable(startLines), var highestFixed: Int = 0, var blockType: Block = Block.MINUS, var blockX: Int = 3, var blockY: Int = 4, var droppedLines: Int = 0) {
        override fun toString(): String {
            return lines.reversed().map { it.map { it.ch }.joinToString("")}.joinToString("\n")+"\nHeight = $highestFixed\n"
        }

        fun insertBlock(block: Block) {
            blockType = block
            blockX = 3
            blockY = 4
        }

        fun moveLeftRight(move: Move): Boolean {
            val collision = checkCollision(blockX + move.changeX, blockY)
            if (collision) return true
            else {
                blockX+=move.changeX
            }
            return false
        }

        fun moveDown(): Boolean {
            val collision = checkCollision(blockX, blockY - 1)
            if (collision) return true
            else {
                blockY-=1
            }
            return false

        }

        fun checkCollision(x: Int, y: Int): Boolean{
            return blockType.indents.withIndex().any {
                val index = highestFixed + y + it.index
                while (index-droppedLines >= (lines.size)) {
                    lines.add( column.toMutableList())
                }
                while (lines.size > 100) {
                    lines.removeAt(0)
                    droppedLines ++
                }
                val checkLine = lines[index-droppedLines]
                (x+ it.value.first until  x + blockType.width - it.value.second).any { pos ->
                    checkLine[pos] == CellState.FIXED
                }
            }
        }

        fun freeze() {
            blockType.indents.withIndex().forEach {
                val index = highestFixed + blockY + it.index
                while (index-droppedLines >= lines.size) {
                    lines.add( column.toMutableList())
                }
                while (lines.size > 100) {
                    lines.removeAt(0)
                    droppedLines ++
                }
                val line = lines[index-droppedLines]
                (blockX+ it.value.first until  blockX + blockType.width - it.value.second).forEach { pos ->
                    line[pos] = CellState.FIXED
                }
            }
            highestFixed = maxOf(highestFixed, highestFixed + blockY + blockType.indents.size - 1)
        }

        companion object {
            private fun makeMutable(lines: List<List<CellState>>): MutableList<MutableList<CellState>> =
                lines.map { it.toMutableList() }.toMutableList()
        }
    }

    private fun runSimulation(parsed: List<Move>, times: Int, times2: Int): Triple<Int, Int, State> {


        val state = State()
        var blockIndex = 0
        var step = 0
        state.insertBlock(Block.values()[blockIndex])

        fun go(
            times: Int,
        ) {
            while (blockIndex < times) {
                state.moveLeftRight(parsed[step++ % parsed.size])
                val freeze = state.moveDown()
                if (freeze) {
                    state.freeze()
                    blockIndex++
                    state.insertBlock(Block.values()[blockIndex % Block.values().size])
                }

            }
        }


        go(times)
        val height1 = state.highestFixed
        go(times2)
        return Triple(height1, state.highestFixed, state)
    }


    fun part1(input: String): Int {
        val parsed = parser.parse(input)

        val result = runSimulation(parsed, 2022, 2022)
        return result.first
    }

    fun part2(input: String): Long {
        val parsed = parser.parse(input)

        val cycleSize = Block.values().size * parsed.size*7
        val afterCycles = (1000000000000 % cycleSize).toInt()
        val nrOfCycles = (1000000000000 / cycleSize)

/*        for (i in 50..150) {
            val result = runSimulation(parsed, cycleSize*2, cycleSize *3)
            val heightPerCycle = (result.second - result.first).toLong() / 1
            println("cycle height: $heightPerCycle")

        }
*/
        val result = runSimulation(parsed, cycleSize*2, cycleSize *3)
        val heightPerCycle = (result.second - result.first).toLong()

        for (i in 1..100) {
            val result2 = runSimulation(parsed, afterCycles + (cycleSize*i), 0)
            val r = heightPerCycle * (nrOfCycles - i) + result2.first
            println("answer: $r, cycle height: $heightPerCycle, cycle size: $cycleSize, number of cycles: $nrOfCycles, index in cycle: $afterCycles, height in cycle: ${result2.first}")

        }
        val result2 = runSimulation(parsed, afterCycles + (cycleSize*5), 0)
        val r = heightPerCycle * (nrOfCycles - 5) + result2.first
        println("answer: $r, cycle height: $heightPerCycle, cycle size: $cycleSize, number of cycles: $nrOfCycles, index in cycle: $afterCycles, height in cycle: ${result2.first}")

        return r
    }

    companion object {
        val column = listOf(CellState.FIXED) + List(7) { CellState.EMPTY} + listOf(CellState.FIXED)
        val startLines = listOf(List(9) { CellState.FIXED}) + List(5) { column }
    }
}

