package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day17Test {

    private val subject = Day17()
    private val year = 2022
    private val day = 17

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 3068) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 3069) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 1514285714288) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true) { part2(input) }
    }
}