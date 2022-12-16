package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day15Test {

    private val subject = Day15()
    private val year = 2022
    private val day = 15

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 26) { part1(testInput, 10) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 5073496) { part1(input, 2000000) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 56000011L) { part2(testInput, 20) }
    }

    //@Test //disable while still brute forced
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 13081194638237) { part2(input, 4000000) }
    }
}