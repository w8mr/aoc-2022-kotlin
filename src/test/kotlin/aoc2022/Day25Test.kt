package aoc2022

import kotlin.test.Test
import aoc.*
import kotlin.test.assertTrue


internal class Day25Test {

    private val subject = Day25()
    private val year = 2022
    private val day = 25

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, "2=-1=0") { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true) { part1(input) }
    }

    @Test
    fun testSnafuToInt7() {
        assertTrue { Day25.Snafu("12").toLong() == 7L }
    }
}