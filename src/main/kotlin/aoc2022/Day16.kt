package aoc2022

import aoc.*

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
            value.tunnelsTo.map { 1 to MapValvesBackedGraphNode(valves.getValue(it), valves) }
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

    private fun setup(input: String): Pair<MapValvesBackedGraphNode, Set<MapValvesBackedGraphNode>> {
        val parsed = valves.parse(input)
        val mapValves = parsed.associateBy { it.name }
        val startValve = mapValves.getValue("AA")

        val startNode = MapValvesBackedGraphNode(startValve, mapValves)
        val openableValves = parsed.filter { it.flowRate > 0 }.map { MapValvesBackedGraphNode(it, mapValves) }.toSet()
        return Pair(startNode, openableValves)
    }
    fun part1(input: String): Int {
        val (graph, openableValves) = setup(input)
        return calcPressure(30, graph, openableValves, 1)
    }

    fun part2(input: String): Int {
        val (graph, openableValves) = setup(input)
        return calcPressure(26, graph, openableValves, 2)
    }
    fun calcPressure(
        timeLeft: Int,
        node: GraphNode<Valve>,
        openableValves: Set<GraphNode<Valve>>,
        players:Int
    ): Int {
        fun go(
            currentTimeLeft: Int,
            currentNode: GraphNode<Valve>,
            currentOpenableValves: Set<GraphNode<Valve>>,
            players:Int
        ): Int =
            if (currentNode is MapValvesBackedGraphNode) {
                maxOf(
                    currentOpenableValves.maxOf { gotoNode ->
                        val distance = currentNode.shortestPaths.getValue(gotoNode)
                        val newTimeLeft = currentTimeLeft - distance - 1
                        val addedPressure = gotoNode.value.flowRate * newTimeLeft
                        val newOpenableValves = currentOpenableValves - setOf(gotoNode)
                        if (newOpenableValves.isEmpty() || (newTimeLeft <= 0)) {
                            addedPressure
                        } else {
                            go(
                                newTimeLeft,
                                gotoNode,
                                newOpenableValves,
                                players
                            ) + addedPressure
                        }
                    },
                    if (players > 1)
                            go(
                                timeLeft,
                                node,
                                currentOpenableValves,
                                players - 1
                            )
                    else 0
                )
            } else {
                throw IllegalStateException()
            }

        return go(timeLeft, node, openableValves, players)
    }
}