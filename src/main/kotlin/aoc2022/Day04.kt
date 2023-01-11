package aoc2022

import aoc.parser.followedBy
import aoc.parser.number
import aoc.parser.seq
import aoc.parser.zeroOrMore

class Day04() {
    data class Zone(val start: Int, val end: Int) {
        operator fun contains(other: Zone): Boolean =
            other.start >= this.start &&
                    other.start <= this.end &&
                    other.end <= this.end &&
                    other.end >= this.start

        infix fun overlap(other: Zone): Boolean =
            (other.start >= this.start && other.start <= this.end) ||
                    (this.start >= other.start && this.start <= other.end)

    }

    val zone = seq(number() followedBy "-", number(), ::Zone)
    val pair = seq(zone followedBy ",", zone followedBy "\n")
    val pairs = zeroOrMore(pair)

    fun part1(input: String): Int {
        return pairs.parse(input).map { (e1, e2) -> e1 in e2 || e2 in e1 }.count { it }
    }

    fun part2(input: String): Int {
        return pairs.parse(input).map { (e1, e2) -> e1 overlap e2 }.count { it }
    }
}

