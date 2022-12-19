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
                        listOf(listOf(node.value) to addedPressure)
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
        return TODO()
    }

}
