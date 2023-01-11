package aoc2022

import aoc.parser.*
import aoc.parser.Parsers.eol
import aoc2022.Day02.Item.*
import aoc2022.Day02.Result.*

class Day02() {
    enum class Item(val name1: String, val name2: String) {
        ROCK("A", "X"),
        PAPER("B", "Y"),
        SCISSOR("C", "Z"),
    }

    enum class Result(val name1: String) {
        LOSE("X"),
        DRAW("Y"),
        WIN("Z")
    }

    val score = mapOf(
        Pair(Pair(ROCK, ROCK), 3 + 1),
        Pair(Pair(ROCK, PAPER), 6 + 2),
        Pair(Pair(ROCK, SCISSOR), 0 + 3),
        Pair(Pair(PAPER, ROCK), 0 + 1),
        Pair(Pair(PAPER, PAPER), 3 + 2),
        Pair(Pair(PAPER, SCISSOR), 6 + 3),
        Pair(Pair(SCISSOR, ROCK), 6 + 1),
        Pair(Pair(SCISSOR, PAPER), 0 + 2),
        Pair(Pair(SCISSOR, SCISSOR), 3 + 3),
    )

    val needToWinMap = mapOf(
        Pair(Pair(ROCK, LOSE), SCISSOR),
        Pair(Pair(ROCK, DRAW), ROCK),
        Pair(Pair(ROCK, WIN), PAPER),
        Pair(Pair(PAPER, LOSE), ROCK),
        Pair(Pair(PAPER, DRAW), PAPER),
        Pair(Pair(PAPER, WIN), SCISSOR),
        Pair(Pair(SCISSOR, LOSE), PAPER),
        Pair(Pair(SCISSOR, DRAW), SCISSOR),
        Pair(Pair(SCISSOR, WIN), ROCK),
    )

    fun scores2(input: Pair<Item, Result>): Int {
        val i2 = needToWinMap[Pair(input.first, input.second)]
        return score[Pair(input.first, i2)] ?: 0
    }

    fun part1(input: String): Int {
        val parsed = parser1(input)
        return parsed.sumOf { score.getValue(it) }
    }

    fun part2(input: String): Int {
        val parsed = parser2(input)
        return parsed.sumOf(::scores2)
    }

    val col1 = Item::class.asParser(Item::name1)
    val col2_part1 = Item::class.asParser(Item::name2)
    val col2_part2 = Result::class.asParser(Result::name1)
    val parser1 = zeroOrMore(seq(col1 and " ", col2_part1 and eol))
    val parser2 = zeroOrMore(seq(col1 and " ", col2_part2 and eol))
}
