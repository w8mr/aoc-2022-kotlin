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
        val distanceNodes = (setOf(node) + openableValves).toList().map{ if (it is MapValvesBackedGraphNode) it else throw IllegalStateException() }
        val distanceMap = distanceNodes.map { distanceNodes.indexOf(it) to it.shortestPaths.mapKeys { e -> distanceNodes.indexOf(e.key) }}.toMap()
        val distanceArray = distanceMap.filterKeys { it >= 0 }.toList().sortedBy { it.first }.map { it.second.filterKeys { it >= 0 }.toList().sortedBy { it.first }.map {it.second}.toIntArray() }
        val countNodes = distanceNodes.size

        fun go(
            currentTimeLeft: Int,
            currentNode: Int,
            currentOpenableValves: Int,
            players:Int,
            depth: Int
        ): Int {
            val newValves = (1 until countNodes).maxOf { gotoNode ->
                if ((currentOpenableValves and (1 shl gotoNode)) == 0)
                    0
                else {
                    val distance = distanceArray[currentNode][gotoNode]
                    val newTimeLeft = currentTimeLeft - distance - 1
                    val addedPressure = distanceNodes[gotoNode].value.flowRate * newTimeLeft
                    val newOpenableValves = currentOpenableValves - (1 shl gotoNode)
                    if (newOpenableValves == 0 || (newTimeLeft <= 1)) {
                        addedPressure
                    } else {
                        go(
                            newTimeLeft,
                            gotoNode,
                            newOpenableValves,
                            players,
                            depth + 1
                        ) + addedPressure
                    }
                }
            }
            return if (players > 1 && (depth >= (countNodes / players)-2))
                maxOf(
                    newValves,
                    go(
                        timeLeft,
                        0,
                        currentOpenableValves,
                        players - 1,
                        depth + 1
                    )
                )
            else
                newValves
        }

        return go(timeLeft, 0, (2 shl countNodes)-2, players, 0)
    }
}