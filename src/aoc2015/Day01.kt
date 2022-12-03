package aoc2015

import aoc.*

fun main() {
    val up = Literal("(").to(1)
    val down = Literal(")").to(-1)
    val instruction = OneOf(up,down)
    val instructions = ZeroOrMore(instruction)


    fun part1(input: String): Int {
        return instructions.parse(input).sum()
    }

    fun part2(input: String): Int {
        return instructions
            .parse(input)
            .scan(0, Int::plus)
            .takeWhile { it >= 0 }
            .size
    }

    check(part1("(())") == 0)
    check(part1("()()") == 0)
    check(part1("(((") == 3)
    check(part1("(()(()(") == 3)
    check(part1("))(((((") == 3)
    check(part1("())") == -1)
    check(part1("))(") == -1)
    check(part1(")))") == -3)
    check(part1(")())())") == -3)

    val input = readFile(2015, 1).readText()
    check(part1(input) == 74)
    println(part1(input))


    check(part2(")") == 1)
    check(part2("()())") == 5)
    check(part2(input) == 1795)
    println(part2(input))
}

