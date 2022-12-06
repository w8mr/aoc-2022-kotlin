package aoc2022

import aoc.readFile

fun main() {
    fun solve(input: String, n: Int) =
        n + input
            .windowed(n)
            .map { it.toSet().size == n }
            .indexOfFirst { it }

    fun part1(input: String): Int {
        return solve(input, 4)
    }

    fun part2(input: String): Int {
        return solve(input, 14)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFile(2022, 6, 1).readText()
    check(part1(testInput) == 7)

    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz") == 5)
    check(part1("nppdvjthqldpwncqszvftbrmjlhg") == 6)
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
    check(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 11)

    check(part2(testInput) == 19)
    check(part2("bvwbjplbgvbhsrlpgdmjqwftvncz") == 23)
    check(part2("nppdvjthqldpwncqszvftbrmjlhg") == 23)
    check(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 29)
    check(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 26)

    val input = readFile(2022, 6).readText()
    check(part1(input) == 1100)
//    check(part2(input) == 201491)
    println(part1(input))
    println(part2(input))
}

