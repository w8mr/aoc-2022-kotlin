package aoc2022

import aoc.*
import aoc.parser.*
import aoc.parser.Parsers.eol

class Day03() {
    fun Char.score() =
        when (this) {
            in 'a'..'z' -> this - 'a' + 1
            in 'A'..'Z' -> this - 'A' + 27
            else -> throw IllegalArgumentException("Only a-z and A-Z allowed")
        }

    fun findCommonItemAndScore(input: List<List<List<Char>>>): Int {
        return input.sumOf { it.reduce(Iterable<Char>::intersect).single().score() }
    }

    fun part1(input: String): Int {
        val parsed = parser(input)
        return findCommonItemAndScore(parsed.map { it.chunked(it.size / 2) })
    }

    fun part2(input: String): Int {
        val parsed = parser(input)
        return findCommonItemAndScore(parsed.chunked(3))
    }

    val bag = regex("[a-zA-Z]*") map String::toList
    val parser = oneOrMore(bag and eol)
}

