package aoc2015

import aoc.parser.asValue
import aoc.parser.oneOrMore
import aoc.parser.or

class Day01() {
    val up = "(" asValue 1
    val down = ")" asValue -1
    val instruction = up or down
    val parser = oneOrMore(instruction)

    fun part1(input: String): Int {
        return parser(input).sum()
    }

    fun part2(input: String): Int {
        return parser(input)
            .scan(0, Int::plus)
            .takeWhile { it >= 0 }
            .size
    }
}
