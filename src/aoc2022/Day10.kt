package aoc2022

import aoc.*
data class Context(var clockcycle: Int = 0, var register: Int = 1)

sealed interface Instruction {
    fun run(context: Context): Context
}
data class Addx(val value: Int) : Instruction {
    override fun run(context: Context): Context {
        return Context(
            context.clockcycle + 2,
            context.register + value)
    }
}
object Noop : Instruction {
    override fun run(context: Context): Context {
        return Context(
            context.clockcycle + 1,
            context.register)
    }
}

val addx = "addx " followedBy number() map ::Addx
val noop = "noop" asValue Noop
val instruction = (addx or noop) + "\n"
val parser = zeroOrMore(instruction)

fun main() {

    fun nextCheckPoint(iter: Iterator<Context>, n: Int, acc: Int): Int {
        var old = 0
        while (iter.hasNext()) {
            val c = iter.next()
            if (c.clockcycle >= n) {
                return acc + n * old
            } else {
                old = c.register
            }
        }
        return acc
    }

    fun part1(input: String): Int {
        val r = parser.parse(input)
        val state = r.scan(Context()) {
            c, instr -> instr.run(c)
        }
        val iter = state.iterator()
        val result = listOf<Int>(20,60,100,140,180,220).fold(0) { acc, n ->
            nextCheckPoint(iter, n, acc)
        }
        return result
    }

    fun part2(input: String): Int {
        return TODO()
    }
    // test if implementation meets criteria from the description, like:/
    val testInput = readFile(2022, 10, 1).readText()
    val input = readFile(2022, 10).readText()
    check(part1(testInput) == 13140)

    println(part1(input))
    check(part1(input) == TODO())

    check(part2(testInput) == TODO())

    println(part2(input))
    check(part2(input) == TODO())

}

private infix fun <R> String.followedBy(parser: Parser<R>): Parser<R> = seq(Literal(this), parser) { _, n -> n }

