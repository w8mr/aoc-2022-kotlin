package aoc2022

import aoc.*
import aoc.parser.*


class Day11 {
    sealed class Argument {
        data class Number(val value: Long) : Argument()
        object Old : Argument()
    }

    data class Monkey(
        val index: Int,
        var items: List<Long>,
        val operator: Pair<(Long, Long) -> Long, Argument>,
        val divisibleBy : Int,
        val ifTrue : Int,
        val ifFalse: Int,
        var inspected: Int = 0)

    //  Monkey 0:
    //    Starting items: 79, 98
    //    Operation: new = old * 19
    //    Test: divisible by 23
    //      If true: throw to monkey 2
    //      If false: throw to monkey 3

    val index = "Monkey " followedBy number() followedBy  ":\n"
    val itemNumbers = number() map { it.toLong() } sepBy ", "
    val items = "  Starting items: " followedBy itemNumbers followedBy  "\n"
    val plus = "+ " asValue { n: Long, m: Long -> n + m }
    val times = "* " asValue { n: Long, m: Long -> n * m }
    val numberArgument = number() map { Argument.Number(it.toLong()) }
    val oldArgument = literal("old") asValue Argument.Old
    val operation = seq("  Operation: new = old " followedBy (plus or times), numberArgument or oldArgument) followedBy  "\n"
    val divisibleBy = "  Test: divisible by " followedBy number() followedBy  "\n"
    val ifTrue = "    If true: throw to monkey " followedBy number() followedBy  "\n"
    val ifFalse = "    If false: throw to monkey " followedBy number() followedBy  "\n"

    val monkey = seq(index, items, operation, divisibleBy, ifTrue, ifFalse, ::Monkey)
    val monkeys = monkey sepBy "\n"

    fun solve(input: String, n: Int, d: Int): Long {
        val parsed = monkeys.parse(input)
        val ring = parsed.map(Monkey::divisibleBy).product()
        (1..n).forEach {
            parsed.forEach {
                it.items.forEach { item ->
                    val right: Long = when (val r = it.operator.second) {
                        is Argument.Number -> r.value
                        is Argument.Old -> item
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
}


