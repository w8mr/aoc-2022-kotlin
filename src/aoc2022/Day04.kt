package aoc2022

import aoc.*

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

fun main() {
    val zone = Seq3(number(),Literal("-"), number()) { s, _, e -> Zone(s,e) }
    val pair = Seq4(zone, Literal(","), zone, Literal("\n")) { e1, _, e2, _ -> Pair(e1, e2) }
    val pairs = ZeroOrMore(pair)

    fun part1(input: String): Int {
        return pairs.parse(input).map { (e1, e2) -> e1 in e2 || e2 in e1 }.count { it }
    }

    fun part2(input: String): Int {
        return pairs.parse(input).map { (e1, e2) -> e1 overlap e2 }.count { it }
    }
    // test if implementation meets criteria from the description, like:
    val testInput = readFile(2022, 4, 1).readText()
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readFile(2022, 4).readText()

    check(part1(input) == 503)
    check(part2(input) == 827)
    println(part1(input))
    println(part2(input))
}

