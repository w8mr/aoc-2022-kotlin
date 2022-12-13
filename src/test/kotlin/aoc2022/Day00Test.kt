package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day00Test {

    private val subject = Day00()
    private val year = 2022
    private val day = 0

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(day, 1, false) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(day, 1, true) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(day, 2, false) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(day, 2, true) { part2(input) }
    }
}