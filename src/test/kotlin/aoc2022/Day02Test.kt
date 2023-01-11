package aoc2022

import kotlin.test.Test
import aoc.*
import kotlin.test.assertTrue


internal class Day02Test {

    private val subject = Day02()
    private val year = 2022
    private val day = 2

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 15) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 11841) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 12) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 13022) { part2(input) }
    }

    @Test
    fun resultWin1() {
        assertTrue { Day02().result(Pair(Day02.Item.SCISSOR, Day02.Item.ROCK)) == Day02.Result.WIN }
    }

    @Test
    fun resultWin2() {
        assertTrue { Day02().result(Pair(Day02.Item.PAPER, Day02.Item.SCISSOR)) == Day02.Result.WIN }
    }

    @Test
    fun resultLose() {
        assertTrue { Day02().result(Pair(Day02.Item.PAPER, Day02.Item.ROCK)) == Day02.Result.LOSE }
    }

    @Test
    fun resultDraw() {
        assertTrue { Day02().result(Pair(Day02.Item.PAPER, Day02.Item.PAPER)) == Day02.Result.DRAW }
    }

    @Test
    fun needsToWin() {
        assertTrue { Day02().needsTo(Pair(Day02.Item.PAPER, Day02.Result.WIN)) == Day02.Item.SCISSOR }
    }

    @Test
    fun needsToDraw() {
        assertTrue { Day02().needsTo(Pair(Day02.Item.PAPER, Day02.Result.DRAW)) == Day02.Item.PAPER }
    }

    @Test
    fun needsToLose() {
        assertTrue { Day02().needsTo(Pair(Day02.Item.PAPER, Day02.Result.LOSE)) == Day02.Item.ROCK }
    }
}