package aoc2022

import aoc.parser.*
import aoc.parser.Parsers.eol
import aoc.parser.Parsers.number

class Day04() {
    operator fun IntRange.contains(other : IntRange) =
        (other.start in this) && (other.endInclusive in this)

    infix fun IntRange.overlap(other : IntRange) =
        (other.start in this) || (start in other)

    val zone = seq(number and "-", number, ::IntRange)
    val pair = seq(zone and ",", zone and eol)
    val parser = oneOrMore(pair)

    fun part1(input: String): Int {
        return parser(input).map { (e1, e2) -> e1 in e2 || e2 in e1 }.count { it }
    }

    fun part2(input: String): Int {
        return parser(input).map { (e1, e2) -> e1 overlap e2 }.count { it }
    }
}

