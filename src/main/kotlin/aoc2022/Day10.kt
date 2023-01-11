package aoc2022

import aoc.parser.*

class Day10 {
    sealed interface Instruction {
        fun run(register: Int): List<Int>
    }
    data class Addx(val value: Int) : Instruction {
        override fun run(register: Int): List<Int> {
            return listOf(register, register + value)
        }
    }
    object Noop : Instruction {
        override fun run(register: Int): List<Int> {
            return listOf(register)
        }
    }

    val addx = "addx " followedBy number() map ::Addx
    val noop = "noop" asValue Noop
    val instruction = addx or noop followedBy  "\n"
    val parser = zeroOrMore(instruction)

    fun calcState(input: String): List<Int> {
        val r = parser.parse(input)
        val state = r.scan(listOf(1)) { c, instr ->
            instr.run(c.last())
        }.flatten()
        return state
    }

    fun part1(input: String): Int {
        val state = calcState(input)
        return (20..220 step 40).map { n ->
            n * state[n - 1]
        }.sum()
    }

    fun part2(input: String): String {
        val state = calcState(input)
        val r = state.chunked(40).map {
            it.mapIndexed { i, r ->
                when (i in ((r - 1)..(r + 1))) {
                    true -> '#'
                    false -> '.'
                }
            }
        }
        return "\n"+r.map { it.joinToString(separator = "") }.joinToString(separator = "\n")
    }
}