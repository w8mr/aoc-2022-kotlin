package aoc2022

import aoc.*

class Day17() {

    enum class Move(val changeX: Int) {
        LEFT(-1),
        RIGHT(1)
    }

    enum class Block(val width: Int, val blockSet: List<Byte>) {
        MINUS(4, listOf(0b1111000)),
        PLUS(3, listOf(0b0100000, 0b1110000, 0b0100000)),
        CORNER(3, listOf(0b1110000, 0b0010000, 0b0010000)),
        LINE(1, listOf(0b1000000, 0b1000000, 0b1000000, 0b1000000)),
        DOT(2, listOf(0b1100000, 0b1100000))
    }

    val left = "<" asValue Move.LEFT
    val right = ">" asValue Move.RIGHT
    val parser = zeroOrMore(left or right) followedBy "\n"


    data class State(
        val lines: ByteArray = ByteArray(100) { if (it == 0) 127 else 0 },
        var highestFixed: Int = 0,
        var blockType: Block = Block.MINUS,
        var blockX: Int = 3,
        var blockY: Int = 4,
        var minY: Int = 0
    ) {
        override fun toString(): String {
            return lines.reversed().map {
                "#${
                    String.format("%7s", Integer.toBinaryString(it.toInt())).replace('0', ' ').replace('1', '#')
                }#"
            }.joinToString("\n") + "\nHeight = $highestFixed\n"
        }

        fun insertBlock(block: Block) {
            blockType = block
            blockX = 3
            blockY = 4

            for (i in 0 until blockType.blockSet.size) {
                lines[(highestFixed + blockY + i) % lines.size] = 0
            }
        }

        fun moveLeftRight(move: Move): Boolean {
            val newX = blockX + move.changeX
            if ((newX < 1) || ((newX + blockType.width) > 8)) return true
            val collision = checkCollision(newX, blockY)
            if (collision) return true
            else {
                blockX = newX
            }
            return false
        }

        fun moveDown(): Boolean {
            val collision = checkCollision(blockX, blockY - 1)
            if (collision) return true
            else {
                blockY -= 1
            }
            return false
        }

        fun checkCollision(x: Int, y: Int): Boolean {
            return blockType.blockSet.withIndex().any {
                val index = highestFixed + y + it.index
                val checkLine = lines[index % lines.size].toInt()
                (it.value.toInt() shr (x - 1) and checkLine) != 0
            }
        }

        fun freeze() {
            blockType.blockSet.withIndex().forEach {
                val index = highestFixed + blockY + it.index
                val line = lines[index % lines.size].toInt()
                val newLine = it.value.toInt() shr (blockX - 1) or (line)
                lines[index % lines.size] = newLine.toByte()
            }
            minY = minOf(minY, blockY)
            highestFixed = maxOf(highestFixed, highestFixed + blockY + blockType.blockSet.size - 1)
        }
    }

    private fun runSimulation(parsed: List<Move>, smallCycleSize: Int, wantedBlock: Long): Pair<Long, State> {
        var newWantedBlock = wantedBlock
        var addedHeight = 0L
        val state = State()
        var blockIndex = 0
        var step = 0
        state.insertBlock(Block.values()[blockIndex])

        var oldHeight = 0
        var oldCycleHeight = 0
        val heightsMap = mutableMapOf<Int, Int>()
        val heights = mutableListOf(0)
        val cycleSizes = mutableMapOf<Int, Int>()
        var cycleNr = 0
        while(true) {
            cycleNr++
            val endCycle = blockIndex + smallCycleSize
            while (blockIndex < endCycle) {
                state.moveLeftRight(parsed[step++ % parsed.size])
                val freeze = state.moveDown()
                if (freeze) {
                    state.freeze()
                    if (blockIndex.toLong() == (newWantedBlock-1)) return (addedHeight + state.highestFixed.toLong()) to state
                    blockIndex++
                    state.insertBlock(Block.values()[blockIndex % Block.values().size])
                    //         println(state)
                }

            }
            val height = state.highestFixed - oldHeight
            heights.add(height)
            val key = height * 17 + oldCycleHeight * 89
            //println("Height $height, index: $cycleNr, key: $key")
            val seen = heightsMap[key]
            if (seen != null) {
                val repeatedCycleSize = cycleNr - seen
                val count = cycleSizes.getOrDefault(repeatedCycleSize, 0) + 1
                cycleSizes[repeatedCycleSize] = count
                if (count > 10) {
                    val cycleHeight = (0 until repeatedCycleSize).sumOf { heights[cycleNr - it] }

                    val largeCycleSize = repeatedCycleSize * smallCycleSize
              //      println("Loop $height, size $repeatedCycleSize $largeCycleSize, count $count, height $cycleHeight")
                    val deleteNrOfCycles = (wantedBlock - blockIndex) / largeCycleSize
                    addedHeight = cycleHeight.toLong() * deleteNrOfCycles
                    newWantedBlock = wantedBlock - deleteNrOfCycles * largeCycleSize
            //        println("delete $deleteNrOfCycles new Wanted block $newWantedBlock added height $addedHeight")

                 //   break
                }
            }
            heightsMap[key] = cycleNr
            oldHeight = state.highestFixed
            oldCycleHeight = height

        }
    }


    fun part1(input: String): Long {
        val parsed = parser.parse(input)
        val smallCycleSize = Block.values().size * parsed.size

        val result = runSimulation(parsed, smallCycleSize, 2022)

        return result.first
    }

    fun part2(input: String): Long {
        val parsed = parser.parse(input)

        val smallCycleSize = Block.values().size * parsed.size

        val result = runSimulation(parsed, smallCycleSize, 1_000_000_000_000)
        return result.first
    }
}
