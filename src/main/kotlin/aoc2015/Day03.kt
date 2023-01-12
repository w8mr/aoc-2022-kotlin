package aoc2015

import aoc.Coord
import aoc.parser.asValue
import aoc.parser.oneOf
import aoc.parser.oneOrMore
import aoc.transpose
import kotlin.reflect.KFunction1

class Day03() {
    val up = "^" asValue Coord::up
    val down = "v" asValue Coord::down
    val right = ">" asValue Coord::right
    val left = "<" asValue Coord::left
    val parser = oneOrMore(oneOf(up, down, right, left))

    fun vistedHouses(i: List<KFunction1<Coord, Coord>>) =
        i.scan(Coord()) { acc, instr -> instr(acc) }.toSet()

    fun countHouses(i: List<KFunction1<Coord, Coord>>) =
        vistedHouses(i).size

    fun part1(input: String): Int {
        return countHouses(parser(input))
    }

    fun part2(input: String): Int {
        return parser.parse(input).chunked(2).transpose()
            .map(::vistedHouses).reduce(Set<Coord>::union).size
    }
}
