package aoc2015

import aoc.*
import aoc.parser.*
import aoc.parser.Parsers.eol
import aoc.parser.Parsers.word

class Day04() {
    val parser = word and eol

    private fun solve(input: String, nrOfZeros: Int): Int {
        val parsed = parser(input)
        val zeros = nrOfZeros * '0'
        return generateSequence(0) { it + 1 }.first { "$parsed$it".md5().startsWith(zeros) }
    }

    fun part1(input: String): Int {
        return solve(input, 5)
    }

    fun part2(input: String): Int {
        return solve(input, 6)
    }
}

