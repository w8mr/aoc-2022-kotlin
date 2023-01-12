package aoc2015

import aoc.readFile
import aoc.testSafe
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class Day05Test {

    private val subject = Day05()
    private val year = 2015
    private val day = 5

    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        assertTrue { subject.isNice1("ugknbfddgicrmopn") }
        assertTrue { subject.isNice1("aaa") }
        assertFalse { subject.isNice1("jchzalrnumimnmhp") }
        assertFalse { subject.isNice1("haegwjzuvuyypxyu") }
        assertFalse { subject.isNice1("ieodomkazucvgmuy") }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 255) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        assertTrue { subject.isNice2("qjhvhtzxzqqjkmpb") }
        assertTrue { subject.isNice2("xxyxx") }
        assertFalse { subject.isNice2("uurcxstgmygtbstg") }
        assertFalse { subject.isNice2("ieodomkazucvgmuy") }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 55) { part2(input) }
    }
}
