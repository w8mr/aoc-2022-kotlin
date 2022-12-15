package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day07Test {

    private val subject = Day07()
    private val year = 2022
    private val day = 7

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 95437) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 1428881) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 24933642) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 10475598) { part2(input) }
    }
}