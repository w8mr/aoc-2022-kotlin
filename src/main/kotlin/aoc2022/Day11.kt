package aoc2022

import aoc.*

data class Monkey(
    val index: Int,
    var items: List<Long>,
    val operator: Pair<(Long, Long) -> Long, OrResult<Long, String>>,
    val divisibleBy : Int,
    val ifTrue : Int,
    val ifFalse: Int,
    var inspected: Int = 0)

fun main() {
    //  Monkey 0:
    //    Starting items: 79, 98
    //    Operation: new = old * 19
    //    Test: divisible by 23
    //      If true: throw to monkey 2
    //      If false: throw to monkey 3

    val index = ("Monkey " followedBy number()) + ":\n"
    val itemNumbers = number() map { it.toLong() } sepBy ", "
    val items = ("  Starting items: " followedBy itemNumbers) + "\n"
    val plus = "+ " asValue { n: Long, m: Long -> n + m }
    val times = "* " asValue { n: Long, m: Long -> n * m }
    val operation = seq("  Operation: new = old " followedBy (plus or times), number() map { it.toLong() } or_ Literal("old")) + "\n"
    val divisibleBy = ("  Test: divisible by " followedBy number()) + "\n"
    val ifTrue = ("    If true: throw to monkey " followedBy number()) + "\n"
    val ifFalse = ("    If false: throw to monkey " followedBy number()) + "\n"

    val monkey = seq(index, items, operation, divisibleBy, ifTrue, ifFalse, ::Monkey)
    val monkeys = monkey sepBy "\n"

    fun solve(input: String, n: Int, d: Int): Long {
        val parsed = monkeys.parse(input)
        val ring = parsed.map(Monkey::divisibleBy).product()
        (1..n).forEach {
            parsed.forEach {
                it.items.forEach { item ->
                    val right: Long = when (val r = it.operator.second) {
                        is OrResult.Left -> r.value
                        is OrResult.Right -> item
                    }
                    val new = (it.operator.first(item, right) / d) % ring
                    if (new % it.divisibleBy == 0L) {
                        parsed[it.ifTrue].items += new
                    } else {
                        parsed[it.ifFalse].items += new
                    }
                }
                it.inspected += it.items.size
                it.items = listOf()
            }
        }
        val inspectedMost = parsed.map(Monkey::inspected).sortedDescending().take(2)
        return inspectedMost[0].toLong() * inspectedMost[1]
    }

    fun part1(input: String): Long {
        return solve(input, 20, 3)
    }

    fun part2(input: String): Long {
        return solve(input, 10000, 1)
    }
    // test if implementation meets criteria from the description, like:/
    val testInput = readFile(2022, 11, 1).readText()
    val input = readFile(2022, 11).readText()
    check(part1(testInput) == 10605L)

    println(part1(input))
    check(part1(input) == 54036L)

    check(part2(testInput) == 2713310158)

    println(part2(input))
    check(part2(input) == 13237873355)

}


