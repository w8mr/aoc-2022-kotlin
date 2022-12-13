package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day01Test {

    private val subject = Day01()
    private val year = 2022
    private val day = 1

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(day, 1, false, 24000) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(day, 1, true, 67622) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(day, 2, false, 45000) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(day, 2, true,201491) { part2(input) }
    }
}