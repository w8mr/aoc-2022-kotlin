package aoc2015

import aoc.readFile
import aoc.testSafe
import kotlin.test.Test
import kotlin.test.assertTrue

internal class Day03Test {

    private val subject = Day03()
    private val year = 2015
    private val day = 3

    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        assertTrue { subject.part1(">\n") == 2 }
        assertTrue { subject.part1("^>v<\n") == 4 }
        assertTrue { subject.part1("^v^v^v^v^v\n") == 2 }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 2592) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        assertTrue { subject.part2("^v\n") == 3 }
        assertTrue { subject.part2("^>v<\n") == 3 }
        assertTrue { subject.part2("^v^v^v^v^v\n") == 11 }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 2360) { part2(input) }
    }
}
