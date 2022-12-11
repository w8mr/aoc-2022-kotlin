package aoc2022

import aoc.*
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
val instruction = (addx or noop) + "\n"
val parser = zeroOrMore(instruction)

fun main() {

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
        return r.map { it.joinToString(separator = "") }.joinToString(separator = "\n")
    }
    // test if implementation meets criteria from the description, like:/
    val testInput = readFile(2022, 10, 1).readText()
    val input = readFile(2022, 10).readText()
    check(part1(testInput) == 13140)

    println(part1(input))
    check(part1(input) == 14540)

    println(part2(input))
}

private infix fun <R> String.followedBy(parser: Parser<R>): Parser<R> = seq(Literal(this), parser) { _, n -> n }

