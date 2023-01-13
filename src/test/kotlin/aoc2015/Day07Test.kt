package aoc2015

import aoc.readFile
import aoc.testSafe
import kotlin.test.Test

internal class Day07Test {

    private val subject = Day07()
    private val year = 2015
    private val day = 7

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 507) { solve(parser(testInput), "e") }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 956) { part1(input) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 40149) { part2(input) }
    }
}
