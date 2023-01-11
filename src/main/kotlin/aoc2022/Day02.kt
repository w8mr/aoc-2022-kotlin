package aoc2022

import aoc.parser.*
import aoc.parser.Parsers.eol

class Day02() {
    enum class Item(val name1: String, val name2: String, val score: Int) {
        ROCK("A", "X", 1),
        PAPER("B", "Y", 2),
        SCISSOR("C", "Z", 3),
    }

    enum class Result(val name1: String, val score: Int) {
        LOSE("X", 0),
        DRAW("Y", 3),
        WIN("Z", 6)
    }

    /**
        pair of items
        @result if second wins, looses or draw of first
     */
    fun result(input: Pair<Item, Item>): Result =
        Result.values()[(input.second.ordinal-input.first.ordinal+1).mod(3)]

    fun needsTo(input: Pair<Item, Result>): Item =
        Item.values()[(input.second.ordinal+input.first.ordinal-1).mod(3)]

    fun score1(input: Pair<Item, Item>) =
        result(input).score + input.second.score

    fun scores2(input: Pair<Item, Result>): Int {
        val you = needsTo(input)
        return score1(Pair(input.first, you))
    }

    fun part1(input: String): Int {
        val parsed = parser1(input)
        return parsed.sumOf(::score1)
    }

    fun part2(input: String): Int {
        val parsed = parser2(input)
        return parsed.sumOf(::scores2)
    }

    val col1 = Item::class.asParser(Item::name1)
    val col2Part1 = Item::class.asParser(Item::name2)
    val col2Part2 = Result::class.asParser(Result::name1)
    val parser1 = zeroOrMore(seq(col1 and " ", col2Part1 and eol))
    val parser2 = zeroOrMore(seq(col1 and " ", col2Part2 and eol))
}
