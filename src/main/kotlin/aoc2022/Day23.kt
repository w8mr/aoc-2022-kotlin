package aoc2022

import aoc.*
import aoc.parser.byEnum
import aoc.parser.followedBy
import aoc.parser.oneOrMore

class Day23() {
    enum class GridType(val text: String) {
        ELF("#"),
        EMPTY(".")
    }

    val type = byEnum(GridType::class, GridType::text)
    val line = oneOrMore(type) followedBy "\n"
    val parser = oneOrMore(line)

    fun proposalNorth(row: Int, col: Int, nw: GridType, n: GridType, ne:GridType, w: GridType, e: GridType, sw: GridType, s: GridType, se: GridType) =
        if (nw == GridType.EMPTY && n == GridType.EMPTY && ne == GridType.EMPTY) Pair(row - 1, col) else null

    fun proposalSouth(row: Int, col: Int, nw: GridType, n: GridType, ne:GridType, w: GridType, e: GridType, sw: GridType, s: GridType, se: GridType) =
        if (sw == GridType.EMPTY && s == GridType.EMPTY && se == GridType.EMPTY) Pair(row + 1, col) else null

    fun proposalWest(row: Int, col: Int, nw: GridType, n: GridType, ne:GridType, w: GridType, e: GridType, sw: GridType, s: GridType, se: GridType) =
        if (nw == GridType.EMPTY && w == GridType.EMPTY && sw == GridType.EMPTY) Pair(row, col - 1) else null

    fun proposalEast(row: Int, col: Int, nw: GridType, n: GridType, ne:GridType, w: GridType, e: GridType, sw: GridType, s: GridType, se: GridType) =
        if (ne == GridType.EMPTY && e == GridType.EMPTY && se == GridType.EMPTY) Pair(row, col + 1) else null

    var proposalFuncs: NeighbourFunctions = sequenceOf(::proposalNorth, ::proposalSouth, ::proposalWest, ::proposalEast)


    private fun round(
        current: List<List<GridType>>,
        proposalFuncs: NeighbourFunctions,
    ): Triple<List<List<GridType>>,
            NeighbourFunctions,
            Map<Pair<Int, Int>, List<Pair<Pair<Int, Int>, Pair<Int, Int>>>>> {
        val rowSize = current[0].size
        val rowsNP = current.asSequence().zipNextPrevious(List(rowSize) { GridType.EMPTY }).toList()
        val proposals = rowsNP.asSequence().flatMapIndexed { row, (rowP, rowC, rowN) ->
            rowP.asSequence().zipNextPrevious(GridType.EMPTY)
                .zip(rowC.asSequence().zipNextPrevious(GridType.EMPTY))
                .zip(rowN.asSequence().zipNextPrevious(GridType.EMPTY)) { p, e -> Triple(p.first, p.second, e) }
                .mapIndexed { col, (rowP, rowC, rowN) ->
                    val (nw, n, ne) = rowP
                    val (w, c, e) = rowC
                    val (sw, s, se) = rowN
                    if (c == GridType.ELF) {
                        if (nw == GridType.EMPTY && n == GridType.EMPTY && ne == GridType.EMPTY && w == GridType.EMPTY &&
                            e == GridType.EMPTY && sw == GridType.EMPTY && s == GridType.EMPTY && se == GridType.EMPTY
                        ) {
                            //  println("${row + 1}, ${col + 1} no neighbour, no move")
                            Pair(row + 1, col + 1) to Pair(row + 1, col + 1)
                        } else {
                            val movedElf: Pair<Int, Int>? =
                                proposalFuncs.map { it.invoke(row + 1, col + 1, nw, n, ne, w, e, sw, s, se) }
                                    .filterNotNull().firstOrNull()
                            if (movedElf == null) {
                                //    println("${row + 1}, ${col + 1} No valid proposal")
                                Pair(row + 1, col + 1) to Pair(row + 1, col + 1)
                            } else {
                                //    println("${row + 1}, ${col + 1} Move $movedElf")
                                Pair(row + 1, col + 1) to movedElf
                            }
                        }
                    } else {
                        //     println("${row + 1}, ${col + 1} no elf, no move")
                        null
                    }
                }
        }.filterNotNull().groupBy { it.second }
        val newPositions = proposals.flatMap {
            if (it.value.size == 1) {
                it.value.map { it.second }
            } else {
                it.value.map { it.first }
            }
        }.toSet()
        val minRow = newPositions.minOf { it.first }
        val maxRow = newPositions.maxOf { it.first }
        val minCol = newPositions.minOf { it.second }
        val maxCol = newPositions.maxOf { it.second }

        val new = (minRow..maxRow).map() { row ->
            (minCol..maxCol).map { col ->
                val elf = Pair(row, col)
                if (elf in newPositions) GridType.ELF
                else GridType.EMPTY
            }
        }
     //   new.map { it.map { it.text }.joinToString(separator = "") }.joinToString(separator = "\n").println()

        val rotatedFuncs = (proposalFuncs.drop(1) + proposalFuncs.take(1)).toList().asSequence()
        return Triple(new, rotatedFuncs, proposals)
    }

    fun part1(input: String): Int {
        val parsed = parser.parse(input)

        val result = (1 .. 10).fold(Triple(parsed, proposalFuncs, mapOf<Pair<Int,Int>,List<Pair<Pair<Int, Int>, Pair<Int, Int>>>>() )) { (current, proposalFuncs, _), _ ->
            round(current, proposalFuncs)
        }

        val elfCount = result.first.sumOf { it.count { it == GridType.ELF} }
        val gridSize = result.first.size * result.first[0].size


        return gridSize - elfCount
    }


    fun part2(input: String): Int {
        val parsed = parser.parse(input)

        var state: Triple<List<List<GridType>>,
                NeighbourFunctions,
                Map<Pair<Int, Int>, List<Pair<Pair<Int, Int>, Pair<Int, Int>>>>> = Triple(parsed, proposalFuncs, mapOf<Pair<Int,Int>,List<Pair<Pair<Int, Int>, Pair<Int, Int>>>>()  )

        var count = 0
        while (true) {
            state = round(state.first, state.second)
            count++
            if (state.third.values.all { it.all { it.first == it.second }}) break
        }

        println(count)
        return  count
    }
}
typealias NeighbourFunction = (Int, Int, Day23.GridType, Day23.GridType, Day23.GridType, Day23.GridType, Day23.GridType, Day23.GridType, Day23.GridType, Day23.GridType) -> Pair<Int, Int>?
typealias NeighbourFunctions = Sequence<NeighbourFunction>


