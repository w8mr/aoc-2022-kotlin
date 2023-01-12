package aoc2015

import kotlin.test.Test
import aoc.*
import kotlin.test.assertTrue


internal class Day01Test {

    private val subject = Day01()
    private val year = 2015
    private val day = 1

    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        assertTrue { subject.part1("(())") == 0 }
        assertTrue { subject.part1("()()") == 0 }
        assertTrue { subject.part1("(((") == 3 }
        assertTrue { subject.part1("(()(()(") == 3 }
        assertTrue { subject.part1("))(((((") == 3 }
        assertTrue { subject.part1("())") == -1 }
        assertTrue { subject.part1("))(") == -1 }
        assertTrue { subject.part1(")))") == -3 }
        assertTrue { subject.part1(")())())") == -3 }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 74) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        assertTrue { subject.part2(")") == 1 }
        assertTrue { subject.part2("()())") == 5 }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 1795) { part2(input) }
    }
}