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
    val col1Map = mapOf(
        Pair("A", Item.ROCK),
        Pair("B", Item.PAPER),
        Pair("C", Item.SCISSOR)
    )

    val col2Map = mapOf(
        Pair("X", Item.ROCK),
        Pair("Y", Item.PAPER),
        Pair("Z", Item.SCISSOR)
    )

    val col2_part2Map = mapOf(
        Pair("X", Result.LOSE),
        Pair("Y", Result.DRAW),
        Pair("Z", Result.WIN)
    )

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


    fun scores(input: String): Int {
        val (c1, c2) = input.split(' ')
        val i1 = col1Map[c1]
        val i2 = col2Map[c2]
        return score[Pair(i1, i2)] ?: 0
    }

    fun scores2(input: String): Int {
        val (c1, c2) = input.split(' ')
        val i1 = col1Map[c1]
        val r2 = col2_part2Map[c2]
        val i2 = needToWinMap[Pair(i1,r2)]
        return score[Pair(i1, i2)] ?: 0
    }

    fun part1(input: List<String>): Int {
        return input.map(::scores).sum()
    }

    fun part2(input: List<String>): Int {
        return input.map(::scores2).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFile(2022, 2, 1).readLines()
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readFile(2022, 2).readLines()
    println(part1(input))
    println(part2(input))
}
