package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day06Test {

    private val subject = Day06()
    private val year = 2022
    private val day = 6

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 7) { part1(testInput) }
    }

    @Test
    fun testPart1ExtraTestInput() {
        subject.testSafe(year, day, 1, false, 5) { part1("bvwbjplbgvbhsrlpgdmjqwftvncz") }
        subject.testSafe(year, day, 1, false, 6) { part1("nppdvjthqldpwncqszvftbrmjlhg") }
        subject.testSafe(year, day, 1, false, 10) { part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") }
        subject.testSafe(year, day, 1, false, 11) { part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 1100) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 19) { part2(testInput) }
    }

    @Test
    fun testPart2ExtraTestInput() {
        subject.testSafe(year, day, 1, false, 23) { part2("bvwbjplbgvbhsrlpgdmjqwftvncz") }
        subject.testSafe(year, day, 1, false, 23) { part2("nppdvjthqldpwncqszvftbrmjlhg") }
        subject.testSafe(year, day, 1, false, 29) { part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") }
        subject.testSafe(year, day, 1, false, 26) { part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 2421) { part2(input) }
    }
}