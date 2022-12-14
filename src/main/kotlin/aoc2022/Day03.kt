package aoc2022

import aoc.*

class Day03() {
    fun Char.score(): Int =
        when (this) {
            in 'a'..'z' -> this - 'a' + 1
            in 'A'..'Z' -> this - 'A' + 27
            else -> throw IllegalArgumentException("Only a-z and A-Z allowed")
        }

    fun findCommonItemAndScore(input: List<List<List<Char>>>): Int {
        return input.sumOf { it.reduce(Iterable<Char>::intersect).single().score() }
    }

    fun part1(input: String): Int {
        val parsed = parser.parse(input)
        return findCommonItemAndScore(parsed.map { it.chunked(it.size / 2) })
    }

    fun part2(input: String): Int {
        val parsed = parser.parse(input)
        return findCommonItemAndScore(parsed.chunked(3))
    }

    val line = regex("[a-zA-Z]*") followedBy "\n"
    val bag = line map String::toList
    val parser = zeroOrMore(bag)
}

