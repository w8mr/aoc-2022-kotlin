package aoc2022

import aoc.*

class Day01() {

    val elf = number() sepBy "\n"
    val elfs = elf sepBy "\n"

    fun sumCaloriesByElf(input: List<List<Int>>) =
        input.map(List<Int>::sum)

    fun part1(input: String): Int {
        val parsed = elfs.parse(input)
        return sumCaloriesByElf(parsed).max()
    }

    fun part2(input: String): Int {
        val parsed = elfs.parse(input)
        return sumCaloriesByElf(parsed).sortedDescending().take(3).sum()
    }

}

