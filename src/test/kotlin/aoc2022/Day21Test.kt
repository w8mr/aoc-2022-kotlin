package aoc2022

import kotlin.test.Test
import aoc.*
import kotlin.test.assertTrue


internal class Day21Test {

    private val subject = Day21()
    private val year = 2022
    private val day = 21

    private val testInput = readFile(year, day, 1).readText()
    private val input = readFile(year, day).readText()

    @Test
    fun testPart1TestInput() {
        subject.testSafe(year, day, 1, false, 152L) { part1(testInput) }
    }

    @Test
    fun testPart1RealInput() {
        subject.testSafe(year, day, 1, true, 87457751482938L) { part1(input) }
    }

//    @Test
    fun testPart2TestInput() {
        subject.testSafe(year, day, 2, false, 301L) { part2(testInput) }
    }

    @Test
    fun testPart2RealInput() {
        subject.testSafe(year, day, 2, true, 3221245824363L) { part2(input) }
    }

    @Test
    fun testNumberMonkey() {
        val numberMonkey = subject.numberMonkey.parse("root: 5")
        assertTrue { numberMonkey.number == 5.0 }
        assertTrue { numberMonkey.name == "root" }
    }

    @Test
    fun testOperationMonkey() {
        val operationMonkey = subject.operationMonkey.parse("root: abcd * efgh")
        assertTrue { operationMonkey.monkey1 == "abcd" }
        assertTrue { operationMonkey.monkey2 == "efgh" }
        assertTrue { operationMonkey.name == "root" }
        assertTrue { operationMonkey.operation == Day21.Operation.TIMES }
    }

    @Test
    fun testMonkeyWithNumber() {
        val numberMonkey = subject.monkey.parse("root: 5\n")
        check(numberMonkey is Day21.NumberMonkey)
        assertTrue { numberMonkey.number == 5.0 }
        assertTrue { numberMonkey.name == "root" }
    }

    @Test
    fun testMonkeyWithOperation() {
        val operationMonkey = subject.monkey.parse("root: abcd * efgh\n")
        check(operationMonkey is Day21.OperationMonkey)
        assertTrue { operationMonkey.monkey1 == "abcd" }
        assertTrue { operationMonkey.monkey2 == "efgh" }
        assertTrue { operationMonkey.name == "root" }
        assertTrue { operationMonkey.operation == Day21.Operation.TIMES }
    }
}