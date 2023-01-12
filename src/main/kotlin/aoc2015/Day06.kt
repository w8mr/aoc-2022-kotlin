package aoc2015

import aoc.parser.Parsers.eol
import aoc.parser.Parsers.number
import aoc.parser.and
import aoc.parser.asParser
import aoc.parser.oneOrMore
import aoc.parser.seq

class Day06 {
    enum class Instruction(val text: String) {
        On("turn on"),
        Off("turn off"),
        Toggle("toggle")
    }

    data class Instr(val instruction: Instruction, val topLeft: Pair<Int, Int>, val bottomRight: Pair<Int, Int>)

    val instruction = Instruction::class.asParser(Instruction::text)
    val coordinate = seq(number and ",", number)
    val line = seq(instruction and " ", coordinate and " through ", coordinate and eol, ::Instr)
    val parser = oneOrMore(line)

    private fun solve(input: String, action: (Instruction, Int) -> Int): IntArray {
        val parsed = parser(input)
        val lights = IntArray(1000 * 1000)
        parsed.forEach { instr ->
            (instr.topLeft.first..instr.bottomRight.first).forEach { row ->
                (instr.topLeft.second..instr.bottomRight.second).forEach { col ->
                    val index = row * 1000 + col
                    lights[index] = action(instr.instruction, lights[index])
                }
            }
        }
        return lights
    }

    fun part1(input: String): Int {
        val lights = solve(input) { instruction, current ->
            when (instruction) {
                Instruction.On -> 1
                Instruction.Off -> 0
                Instruction.Toggle -> 1 - current
            }
        }
        return lights.count { it == 1 }
    }

    fun part2(input: String): Int {
        val lights = solve(input) { instruction, current ->
            when (instruction) {
                Instruction.On -> current + 1
                Instruction.Off -> maxOf(0, current - 1)
                Instruction.Toggle -> current + 2
            }
        }
        return lights.sum()
    }
}
