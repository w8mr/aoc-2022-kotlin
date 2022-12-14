package aoc2022

import kotlin.test.Test
import aoc.*
import kotlin.test.assertEquals


internal class Day14Test {

    private val subject = Day14()
    private val year = 2022
    private val day = 14

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 24) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 795) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 93) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true) { part2(input) }
    }

    @Test
    fun testCreateLine() {
        with (subject) {
            val input = listOf(Coord(2,25), Coord(5,25), Coord(5,15))
            val result = createLine(input)
            assertEquals(listOf(Day14.Line(Coord(2, 25), Coord(5, 25)), Day14.Line(Coord(5,25), Coord(5,15))), result)
        }
    }

    @Test
    fun testLineParser() {
        with (subject) {
            val result = line.parse("2,25 -> 5,25 -> 5,15\n")
            assertEquals(listOf(Day14.Line(Coord(2, 25), Coord(5, 25)), Day14.Line(Coord(5,25), Coord(5,15))), result)
        }
    }

    @Test
    fun testCoordParser() {
        with (subject) {
            val result = coord.parse("2,25")
            assertEquals(Coord(2, 25), result)
        }
    }
}