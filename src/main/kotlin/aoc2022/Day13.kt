package aoc2022

import aoc.parser.*
import java.lang.IllegalStateException

class Day13 {
    sealed class ElementOrList : Comparable<ElementOrList> {


        class E(val value: Int) : ElementOrList()
        class L(val list: List<ElementOrList>) : ElementOrList()

        override fun compareTo(other: ElementOrList): Int {
            return when {
                this is E && other is E -> {
                    when {
                        value == other.value -> 0
                        value < other.value -> -1
                        else -> 1
                    }
                }

                this is L && other is E ->
                    compareTo(L(listOf(other)))

                this is E && other is L ->
                    L(listOf(this)).compareTo(other)

                this is L && other is L -> {
                    val notEqual: Int? = list.zip(other.list).map { (f, s) -> f.compareTo(s) }.find { it != 0 }
                    when {
                        notEqual != null -> notEqual
                        else -> when {
                            list.size == other.list.size -> 0
                            list.size < other.list.size -> -1
                            else -> 1
                        }
                    }
                }

                else -> throw IllegalStateException()
            }
        }
    }

    val element = number() map ElementOrList::E
    val list: Parser<ElementOrList.L> = ("[" followedBy (element or ref(::list) sepBy ",") followedBy  "]") map ElementOrList::L
    val pair = seq(list followedBy "\n", list followedBy "\n")
    val pairs = pair sepBy "\n"


    fun part1(input: String): Int {
        val p = pairs.parse(input)
        val r = p.map { (f, s) -> f.compareTo(s)}

        return r.withIndex().filter { (_, v) -> v <= 0}.sumOf { (i, _) -> i+1}
    }

    fun part2(input: String): Int {
        val p = pairs.parse(input).flatMap { it.toList() }
        val marker1 = ElementOrList.L(listOf(ElementOrList.E(2)))
        val marker2 = ElementOrList.L(listOf(ElementOrList.E(6)))
        val n = p + listOf(marker1, marker2)

        val set = n.toSortedSet()
        val i1 = set.indexOf(marker1) + 1
        val i2 = set.indexOf(marker2) + 1
        return i1 * i2
    }
}

