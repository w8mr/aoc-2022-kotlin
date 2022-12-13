package aoc2022

import aoc.*
import java.lang.IllegalStateException

sealed interface ElementOrList {
    class E(val value: Int) : ElementOrList
    class L(val list: List<ElementOrList>) : ElementOrList
}

val element = number() map ElementOrList::E
val list: Parser<ElementOrList.L> = (("[" followedBy (element or ref(::list) sepBy ",")) + "]") map ElementOrList::L
val pair = seq(list + "\n", list + "\n")
val pairs = pair sepBy "\n"



fun main() {

    fun compare(input: Pair<ElementOrList, ElementOrList>): Int {
        val (first, second) = input
        return when {
            first is ElementOrList.E && second is ElementOrList.E -> {
                when {
                    first.value == second.value -> 0
                    first.value < second.value -> -1
                    else -> 1
                }
            }

            first is ElementOrList.L && second is ElementOrList.E ->
                compare(first to ElementOrList.L(listOf(second)))

            first is ElementOrList.E && second is ElementOrList.L ->
                compare(ElementOrList.L(listOf(first)) to second)

            first is ElementOrList.L && second is ElementOrList.L -> {
                val notEqual = first.list.zip(second.list).map { compare(it) }.find { it != 0 }
                when {
                    notEqual != null -> notEqual
                    else -> when {
                        first.list.size == second.list.size -> 0
                        first.list.size < second.list.size -> -1
                        else -> 1
                    }
                }
            }

            else -> throw IllegalStateException()
        }

    }

    fun part1(input: String): Int {
        val p = pairs.parse(input)
        val r = p.map(::compare)

        return r.withIndex().filter { (_, v) -> v <= 0}.sumOf { (i, _) -> i+1}
    }

    fun part2(input: String): Int {
        return TODO()
    }
    // test if implementation meets criteria from the description, like:/
    val testInput = readFile(2022, 13, 1).readText()
    val input = readFile(2022, 13).readText()
    check(part1(testInput) == 13)

    println(part1(input))
    check(part1(input) == TODO())

    check(part2(testInput) == TODO())

    println(part2(input))
    check(part2(input) == TODO())

}

