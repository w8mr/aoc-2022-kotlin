package aoc2022

import aoc.*
data class Context(var clockcycle: Int = 0, var register: Int = 1)

sealed interface Instruction {
    fun run(context: Context): List<Context>
}
data class Addx(val value: Int) : Instruction {
    override fun run(context: Context): List<Context> {
        return listOf(context.copy(clockcycle = context.clockcycle + 1), Context(
            context.clockcycle + 2,
            context.register + value))
    }
}
object Noop : Instruction {
    override fun run(context: Context): List<Context> {
        return listOf(context.copy(clockcycle =
            context.clockcycle + 1))
    }
}

val addx = "addx " followedBy number() map ::Addx
val noop = "noop" asValue Noop
val instruction = (addx or noop) + "\n"
val parser = zeroOrMore(instruction)

fun main() {

    fun calcState(input: String): List<Context> {
        val r = parser.parse(input)
        val state = r.scan(listOf(Context())) { c, instr ->
            instr.run(c.last())
        }.flatten()
        return state
    }

    fun part1(input: String): Int {
        val state = calcState(input)
        return (20..220 step 40).map { n ->
            n * state[n - 1].register
        }.sum()
    }

    fun part2(input: String): String {
        val state = calcState(input)
        val r = state.chunked(40).map {
            it.mapIndexed { i, r ->
                when (i in ((r.register - 1)..(r.register + 1))) {
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

