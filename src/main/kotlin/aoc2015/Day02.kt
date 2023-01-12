package aoc2015

import aoc.parser.Parsers.eol
import aoc.parser.Parsers.number
import aoc.parser.and
import aoc.parser.oneOrMore
import aoc.parser.seq

class Day02() {
    data class Box(val l: Int, val w: Int, val h: Int) {
        fun wrappingPaper(): Int {
            return 2 * (l * w + l * h + w * h) + Math.min(Math.min(l * w, l * h), h * w)
        }
        fun ribbonLint(): Int {
            return 2 * Math.min(Math.min(l + w, l + h), h + w) + l * w * h
        }
    }

    val box = seq(
        number and "x",
        number and "x",
        number and eol,
        ::Box
    )
    val parser = oneOrMore(box)

    fun part1(input: String): Int {
        return parser(input).map(Box::wrappingPaper).sum()
    }

    fun part2(input: String): Int {
        return parser(input).map(Box::ribbonLint).sum()
    }
}
