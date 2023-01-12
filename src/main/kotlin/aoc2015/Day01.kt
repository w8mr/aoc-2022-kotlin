package aoc2015

import aoc.*
import aoc.parser.asValue
import aoc.parser.oneOrMore
import aoc.parser.or

fun main() {
    val up = "(" asValue 1
    val down = ")" asValue -1
    val instruction = up or down
    val instructions = oneOrMore(instruction)


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

