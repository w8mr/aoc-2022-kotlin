package aoc2015

import aoc.parser.Parsers.eol
import aoc.parser.Parsers.word
import aoc.parser.and
import aoc.parser.oneOrMore
import aoc.same

class Day05() {
    val parser = oneOrMore(word and eol)

    fun isNice1(word: String) =
        (word.count { it in "aeiou" } >= 3) &&
            (word.zipWithNext().count { it.first == it.second } >= 1) &&
            (word.zipWithNext().none { it.toList().joinToString("") in listOf("ab", "cd", "pq", "xy") })

    fun part1(input: String): Int {
        return parser(input).count(::isNice1)
    }

    fun isNice2(word: String) =
        (
            word.zipWithNext().filterIndexed { index, it ->
                val idx = word.indexOf(it.toList().joinToString(""), index + 2)
                (idx != -1) && (idx != index)
            }.firstOrNull() != null
            ) && (
            word.zip(word.drop(2)).any(Pair<Char, Char>::same)
            )

    fun part2(input: String): Int {
        return parser(input).count(::isNice2)
    }
}
