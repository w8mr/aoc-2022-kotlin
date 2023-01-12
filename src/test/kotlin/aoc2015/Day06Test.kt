package aoc2015

import aoc.readFile
import aoc.testSafe
import kotlin.test.Test

internal class Day06Test {

    private val subject = Day06()
    private val year = 2015
    private val day = 6

    private val input = readFile(year, day).readText()

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 543903) { part1(input) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 14687245) { part2(input) }
    }
}
