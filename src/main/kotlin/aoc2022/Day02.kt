package aoc2022

import aoc.*

class Day02() {
    enum class Item {
        ROCK,
        PAPER,
        SCISSOR
    }

    enum class Result {
        LOSE,
        DRAW,
        WIN
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
        val parser = zeroOrMore(seq(col1+" ", col2_part1+"\n"))
        val parsed = parser.parse(input)
        return parsed.map { score[it] ?: 0 }.sum()
    }

    fun part2(input: String): Int {
        val parser = zeroOrMore(seq(col1+" ", col2_part2+"\n"))
        val parsed = parser.parse(input)
        return parsed.map(::scores2).sum()
    }

    val col1 = oneOf(
        "A" asValue Item.ROCK,
        "B" asValue Item.PAPER,
        "C" asValue Item.SCISSOR
    )
    val col2_part1 = oneOf(
        "X" asValue Item.ROCK,
        "Y" asValue Item.PAPER,
        "Z" asValue Item.SCISSOR
    )

    val col2_part2 = oneOf(
        "X" asValue Result.LOSE,
        "Y" asValue Result.DRAW,
        "Z" asValue Result.WIN
    )
}
