package aoc2022

import aoc.*

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
        Pair(Pair(Item.ROCK, Item.ROCK), 3 + 1),
        Pair(Pair(Item.ROCK, Item.PAPER), 6 + 2),
        Pair(Pair(Item.ROCK, Item.SCISSOR), 0 + 3),
        Pair(Pair(Item.PAPER, Item.ROCK), 0 + 1),
        Pair(Pair(Item.PAPER, Item.PAPER), 3 + 2),
        Pair(Pair(Item.PAPER, Item.SCISSOR), 6 + 3),
        Pair(Pair(Item.SCISSOR, Item.ROCK), 6 + 1),
        Pair(Pair(Item.SCISSOR, Item.PAPER), 0 + 2),
        Pair(Pair(Item.SCISSOR, Item.SCISSOR), 3 + 3),
    )

    val needToWinMap = mapOf(
        Pair(Pair(Item.ROCK, Result.LOSE), Item.SCISSOR),
        Pair(Pair(Item.ROCK, Result.DRAW), Item.ROCK),
        Pair(Pair(Item.ROCK, Result.WIN), Item.PAPER),
        Pair(Pair(Item.PAPER, Result.LOSE), Item.ROCK),
        Pair(Pair(Item.PAPER, Result.DRAW), Item.PAPER),
        Pair(Pair(Item.PAPER, Result.WIN), Item.SCISSOR),
        Pair(Pair(Item.SCISSOR, Result.LOSE), Item.PAPER),
        Pair(Pair(Item.SCISSOR, Result.DRAW), Item.SCISSOR),
        Pair(Pair(Item.SCISSOR, Result.WIN), Item.ROCK),
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

    val col1 = byEnum(Item::class, Item::name1)
    val col2_part1 = byEnum(Item::class, Item::name2)
    val col2_part2 = byEnum(Result::class, Result::name1)
    val parser1 = zeroOrMore(seq(col1 followedBy " ", col2_part1 followedBy "\n"))
    val parser2 = zeroOrMore(seq(col1 followedBy " ", col2_part2 followedBy "\n"))
}
