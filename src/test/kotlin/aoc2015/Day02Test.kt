package aoc2015

import aoc.readFile
import aoc.testSafe
import kotlin.test.Test
import kotlin.test.assertTrue

internal class Day02Test {

    private val subject = Day02()
    private val year = 2015
    private val day = 2

    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        assertTrue { subject.part1("2x3x4\n") == 58 }
        assertTrue { subject.part1("1x1x10\n") == 43 }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 1588178) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        assertTrue { subject.part2("2x3x4\n") == 34 }
        assertTrue { subject.part2("1x1x10\n") == 14 }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 3783758) { part2(input) }
    }
}
