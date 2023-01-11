package aoc2022

import aoc.parser.followedBy
import aoc.parser.number
import aoc.parser.zeroOrMore

class Day20() {
    val parser = zeroOrMore(number() followedBy "\n")

    private fun solve(parsed: List<Long>, repeat: Long): Long {
        val size = parsed.size
        val sizem1 = size - 1
        //println(size)
        //println(parsed)

        val indexes = List(size) { it }.toMutableList()

        (0 until repeat).forEach {
            parsed.forEachIndexed { idx, move ->
                val from = indexes.indexOf(idx)
                val to = (from + move).mod(sizem1)
                val e = indexes.removeAt(from)
                indexes.add(to, e)

            }
  //          val list = indexes.map { parsed[it] }
  //          println(list)
        }
        val list = indexes.map { parsed[it] }


        val indexOf0 = list.indexOf(0)

        return listOf(1000, 2000, 3000).sumOf {
            list[(indexOf0 + it).mod(size)]
        }
    }

    fun part1(input: String): Long {
        val parsed = parser.parse(input).map { it.toLong() }
        return solve(parsed, 1)
    }

    fun part2(input: String): Long {
        val parsed = parser.parse(input).map { it.toLong() * 811589153 }
        return solve(parsed, 10)
    }
}

