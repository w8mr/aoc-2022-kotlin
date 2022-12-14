package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day03Test {

    private val subject = Day03()
    private val year = 2022
    private val day = 3

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 157) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 7863) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 70) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 2488) { part2(input) }
    }
}