package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day09Test {

    private val subject = Day09()
    private val year = 2022
    private val day = 9

    private val testInput = readFile(year, day, 1).readText()
    private val testInput2 = readFile(year, day, 2).readText()

    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 13) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 6269) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 1) { part2(testInput) }
    }

    @Test
    fun testPart2Test2Input() {
        subject.testSafe(year, day, 2, false, 36) { part2(testInput2) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 2557) { part2(input) }
    }
}