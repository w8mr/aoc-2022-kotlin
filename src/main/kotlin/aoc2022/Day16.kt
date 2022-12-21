package aoc2022

import aoc.*
import aoc.Regex

class Day16() {
    data class Valve(val name: String, val flowRate: Int, val tunnelsTo: List<String>) {
        override fun toString() = "$name($flowRate)"
    }


    //Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
    val valve = seq(
        "Valve " followedBy Regex("[A-Z]{2}"),
        " has flow rate=" followedBy number(),
        seq(
            Literal("; tunnel leads to valve ") or Literal("; tunnels lead to valves "),
            Regex("[A-Z]{2}") sepBy ", ", ::second
        ),
        ::Valve) followedBy "\n"

    val valves = zeroOrMore(valve)

    class MapValvesBackedGraphNode(override val value: Valve, private val valves: Map<String, Valve>): GraphNode<Valve> {
        override val neighbours by lazy {
            fun findNeighbours(node: Valve, checkedValves: Set<Valve>): List<Pair<Int, GraphNode<Valve>>> {
                val reachable = node.tunnelsTo.map { valves.getValue(it) }
                val (hasPressure, hasNoPressure) = reachable.partition { it.flowRate > 0 }
                val direct = hasPressure.map { 1 to MapValvesBackedGraphNode(it, valves)}
                val indirect = hasNoPressure.filter { it !in checkedValves }.flatMap { findNeighbours(it, setOf(node)+ checkedValves + hasPressure.toSet()).map { (first, second) -> first + 1 to second} }
                return direct + indirect
            }
            findNeighbours(value, setOf())
        }

        override fun toString(): String {
            return value.toString()
        }

        override fun equals(other: Any?): Boolean =
            when (other) {
                is MapValvesBackedGraphNode -> other.valves == this.valves && other.value == this.value
                else -> false
            }

        override fun hashCode(): Int {
            return this.value.hashCode()
        }

        val shortestPaths by lazy {
            findShortestPaths(this).mapValues { it.value.first }
        }
    }

    fun part1(input: String): Int {
        val (graph, openableValves) = setup(input)
        val pressure = calcPressure(30, graph, openableValves).sortedByDescending { it.second }
        println(pressure.take(3).map { it.second to it.first.map { it.name } })


        return pressure.take(1).map { it.second }.single()
    }

    private fun setup(input: String): Pair<MapValvesBackedGraphNode, Set<MapValvesBackedGraphNode>> {
        val parsed = valves.parse(input)
        val mapValves = parsed.associateBy { it.name }
        val startValve = mapValves.getValue("AA")

        val startNode = MapValvesBackedGraphNode(startValve, mapValves)
        val openableValves = parsed.filter { it.flowRate > 0 }.map { MapValvesBackedGraphNode(it, mapValves) }.toSet()
   //     println(openableValves)
        return Pair(startNode, openableValves)
    }

    fun calcPressure(timeLeft: Int, node: GraphNode<Valve>, openableValves: Set<GraphNode<Valve>>): List<Pair<List<Valve>,Int>> {
        return openableValves.flatMap {
            if (node is MapValvesBackedGraphNode) {
                val distance = node.shortestPaths.getValue(it)
                val newTimeLeft = timeLeft - distance - 1
                val addedPressure = newTimeLeft * it.value.flowRate
                val newOpenableValves = openableValves - setOf(it)
                if (newOpenableValves.isEmpty() || (newTimeLeft <= 0)) {
                        listOf(listOf(it.value) to addedPressure)
                } else {
                    val recusive = calcPressure(newTimeLeft, it, newOpenableValves)
                    recusive.map { (parents, pressure) ->
                        (listOf(it.value) + parents) to (pressure + addedPressure)
                    }
                }
            } else {
                throw IllegalStateException()
            }
        }
    }

    fun part2(input: String): Int {
        val (graph, openableValves) = setup(input)
        val pressure = calcPressure2(26, 26, graph, graph, openableValves).sortedByDescending { it.first }

        //pressure.filter{ it.first.map { it.valve.name}.equals(listOf("JJ","BB","CC")) && it.second.map { it.valve.name}.equals(listOf("DD","HH","EE"))}.map{ it.toList().sumOf { it.sumOf{ it.added()} } to it.toList().map{ it.map { it to it.added()} }}.forEach(::println)
        pressure.take(50).map{ it.first to listOf(it.second, it.third).map{ it.map { it to it.added()} }}.forEach(::println)


        //println (pressure.take(1).single().first.first.map{ MapValvesBackedGraphNode(it, }.scan(0 to graph) { (_, n1), n2 -> n1.shortestPaths[n2]!! to n2})
        return TODO()
    }


    data class Pressure(val valve: Valve, val timeLeft: Int) {
        fun added() = this.timeLeft * this.valve.flowRate

    }
    fun calcPressure2(
        timeLeftYou: Int,
        timeLeftElephant: Int,
        nodeYou: GraphNode<Valve>,
        nodeElephant: GraphNode<Valve>,
        openableValves: Set<GraphNode<Valve>>
    ): List<Triple<Int, List<Pressure>, List<Pressure>>> {
        return openableValves.flatMap { goto ->
                you(nodeYou, goto, timeLeftYou, openableValves, timeLeftElephant, nodeElephant) +
                elephant(nodeElephant, goto, timeLeftElephant, openableValves, timeLeftYou, nodeYou)
        }
    }

    private fun you(
        nodeYou: GraphNode<Valve>,
        goto: GraphNode<Valve>,
        timeLeftYou: Int,
        openableValves: Set<GraphNode<Valve>>,
        timeLeftElephant: Int,
        nodeElephant: GraphNode<Valve>
    ) = if (nodeYou is MapValvesBackedGraphNode) {
        val distance = nodeYou.shortestPaths.getValue(goto)
        val newTimeLeft = timeLeftYou - distance - 1
        val addedPressure = Pressure(goto.value, newTimeLeft)
        val newOpenableValves = openableValves - setOf(goto)
        if (newOpenableValves.isEmpty() || (newTimeLeft <= 0)) {
            listOf(Triple(addedPressure.added(), listOf(addedPressure), listOf()))
        } else {
            val recusive = calcPressure2(
                newTimeLeft,
                timeLeftElephant,
                goto,
                nodeElephant,
                newOpenableValves
            )
            recusive.map { (total, pressureYou, pressureElephant) ->
                Triple(total + addedPressure.added(), listOf(addedPressure) + pressureYou, pressureElephant)
            }
        }
    } else {
        throw IllegalStateException()
    }
    private fun elephant(
        nodeElephant: GraphNode<Valve>,
        goto: GraphNode<Valve>,
        timeLeftElephant: Int,
        openableValves: Set<GraphNode<Valve>>,
        timeLeftYou: Int,
        nodeYou: GraphNode<Valve>
    ) = if (nodeElephant is MapValvesBackedGraphNode) {
        val distance = nodeElephant.shortestPaths.getValue(goto)
        val newTimeLeft = timeLeftElephant - distance - 1
        val addedPressure = Pressure(goto.value, newTimeLeft)
        val newOpenableValves = openableValves - setOf(goto)
        if (newOpenableValves.isEmpty() || (newTimeLeft <= 0)) {
            listOf(Triple(addedPressure.added(), listOf(), listOf(addedPressure)))
        } else {
            val recusive = calcPressure2(
                    timeLeftYou,
                    newTimeLeft,
                    nodeYou,
                    goto,
                    newOpenableValves
                )
            recusive.map { (total, pressureYou, pressureElephant) ->
                Triple(total + addedPressure.added(), pressureYou, listOf(addedPressure) + pressureElephant)
            }
        }
    } else {
        throw IllegalStateException()
    }
}

fun main() {
    val input = readFile(2022, 16).readText()
    Day16().part2(input)
}
/*
AA -> JJ 2+1: 23*21 483
JJ -> BB 2+1  20*13 260
BB -> CC 1+1  18*2   36

AA -> DD 1+1: 24*20 480
DD -> EE 1+1: 22*3   66
EE -> HH 3+1: 18*22 396

                   1721


 */