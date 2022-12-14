package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day05Test {

    private val subject = Day05()
    private val year = 2022
    private val day = 5

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, "CMZ") { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, "PSNRGBTFT") { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, "MCD") { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, "BNTZFPMMW") { part2(input) }
    }
}