package aoc2022

import aoc.*
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException

sealed class Elevation(val level: Int)
class Level(level: Int): Elevation(level) {
    override fun toString(): String = "Level $level"
}
class Start: Elevation(0) {
    override fun toString(): String = "Start"
}
class End: Elevation(25) {
    override fun toString(): String = "End"
}

fun elevation(char: String): Elevation {
    if (char.length != 1) throw IllegalArgumentException()
    return when (val c = char[0]) {
        'S' -> Start()
        'E' -> End()
        else -> Level(c - 'a')
    }
}

fun <T> Grid<T>.find(predicate: (T)  -> Boolean): Coord? {
    this.forEachIndexed { y, inner ->
        inner.forEachIndexed { x, element ->
            if (predicate(element)) {
                return Coord(x, y)
            }
        }
    }
    return null
}

typealias Grid<T> = Array<Array<T>>
data class GridBackedGraphNode<T>(private val gridBack: GridBack<T>, private val coord: Coord) : GraphNode<T> {
    override val neighbours by lazy { gridBack.neighbourNodes(gridBack, coord) }
    override val value: T by lazy { gridBack.grid.fromCoord(coord)!! }
}

class GridBack<T>(val grid: Grid<T>, val neighbourNodes: (GridBack<T>, Coord) -> List<Pair<Int, GraphNode<T>>>)

fun <T> Grid<T>.toDAG(start:Coord, neighbourNodes: (GridBack<T>, Coord) -> List<Pair<Int, GraphNode<T>>>): GraphNode<T> {
    return GridBackedGraphNode(GridBack(this, neighbourNodes), start)
}

fun <T> Grid<T>.fromCoord(coord:Coord): T? {
    return try {
        this[coord.y][coord.x]
    } catch (iobe : IndexOutOfBoundsException) {
        null
    }
}


fun main() {
    val parser = zeroOrMore(
        (zeroOrMore(
            (regex("[a-zSE]") map(::elevation))) + "\n").asArray()
    ).asArray()

    fun part1(input: String): Int {
        val grid = parser.parse(input)
        val start = (grid.find { it is Start })!!
        val dag = grid.toDAG(start) { g, c ->
            val t0 = g.grid.fromCoord(c)!!
            val neighbours = c.dirs4()
                .map { c4 -> c4 to g.grid.fromCoord(c4) }
                .filter {
                    (it.second?.level ?: Int.MAX_VALUE) - t0.level <= 1
                }
                .map { Pair(1, GridBackedGraphNode(g, it.first)) }
            neighbours
        }


        return findShortestPath(dag) { (it as GridBackedGraphNode).value is End }!!
    }

    fun part2(input: String): Int? {
        val grid = parser.parse(input)
        val end = (grid.find { it is End })!!
        val dag = grid.toDAG(end) { g, c ->
            val t0 = g.grid.fromCoord(c)!!
            val neighbours = c.dirs4()
                .map { c4 -> c4 to g.grid.fromCoord(c4) }
                .filter {
                    it.second != null && ((it.second?.level ?: 0) - t0.level  >= -1)
                }
                .map { Pair(1, GridBackedGraphNode(g, it.first)) }
            neighbours
        }


        return findShortestPath(dag) {
            val node = (it as GridBackedGraphNode).value
            if (node is Level) {
                node.level == 0
            } else {
                false
            }
        }!!
    }
    // test if implementation meets criteria from the description, like:/
    val testInput = readFile(2022, 12, 1).readText()
    val input = readFile(2022, 12).readText()
    check(part1(testInput) == 31)

    println(part1(input))
    check(part1(input) == 517)

    check(part2(testInput) == 29)

    println(part2(input))
    check(part2(input) == 512)

}


