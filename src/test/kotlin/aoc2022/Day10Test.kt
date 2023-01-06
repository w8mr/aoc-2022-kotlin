package aoc2022

import kotlin.test.Test
import aoc.*


internal class Day10Test {

    private val subject = Day10()
    private val year = 2022
    private val day = 10

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 13140) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 14540) { part1(input) }
    }

    @Test
    fun testPart2TestInput() {
        val expected = """

            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
            .""".trimIndent()
        subject.testSafe(year, day, 2, false, expected) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        val expected = """

            ####.#..#.####.####.####.#..#..##..####.
            #....#..#....#.#.......#.#..#.#..#....#.
            ###..####...#..###....#..####.#......#..
            #....#..#..#...#.....#...#..#.#.....#...
            #....#..#.#....#....#....#..#.#..#.#....
            ####.#..#.####.#....####.#..#..##..####.
            .""".trimIndent()
        subject.testSafe(year, day, 2, true, expected) { part2(input) }
    }
}