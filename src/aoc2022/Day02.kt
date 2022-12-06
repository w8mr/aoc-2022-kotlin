package aoc2022

import aoc.*

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

fun main() {
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

    fun part1(input: List<Pair<Item, Item>>): Int {
        return input.map { score[it] ?: 0 }.sum()
    }

    fun part2(input: List<Pair<Item, Result>>): Int {
        return input.map(::scores2).sum()
    }

    val col1 = OneOf(
        Literal("A").to(Item.ROCK),
        Literal("B").to(Item.PAPER),
        Literal("C").to(Item.SCISSOR)
    )
    val col2_part1 = OneOf(
        Literal("X").to(Item.ROCK),
        Literal("Y").to(Item.PAPER),
        Literal("Z").to(Item.SCISSOR)
    )

    val col2_part2 = OneOf(
        Literal("X").to(Result.LOSE),
        Literal("Y").to(Result.DRAW),
        Literal("Z").to(Result.WIN)
    )

    val parser1 = ZeroOrMore(seq(col1, Literal(" "), col2_part1, Literal("\n")) { c1, _, c2, _ -> Pair(c1, c2) })
    val parser2 = ZeroOrMore(seq(col1, Literal(" "), col2_part2, Literal("\n")) { c1, _, c2, _ -> Pair(c1, c2) })
    // test if implementation meets criteria from the description, like:
    val testInput1 = parser1.parse(readFile(2022, 2, 1).readText())
    val testInput2 = parser2.parse(readFile(2022, 2, 1).readText())
    check(part1(testInput1) == 15)
    check(part2(testInput2) == 12)

    val input1 = parser1.parse(readFile(2022, 2).readText())
    println(part1(input1))
    val input2 = parser2.parse(readFile(2022, 2).readText())
    println(part2(input2))
}
