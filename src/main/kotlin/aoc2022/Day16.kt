package aoc2022

import aoc.*
import kotlin.math.absoluteValue

private const val empty: Int = Int.MAX_VALUE

class Day16() {
    data class Valve(val name: String, val flowRate: Int, val tunnelsTo: List<String>) {
        override fun toString() = "$name($flowRate)"
    }

    //Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
    val valve = seq(
        "Valve " followedBy regex("[A-Z]{2}"),
        " has flow rate=" followedBy number(),
        seq(
            literal("; tunnel leads to valve ") or literal("; tunnels lead to valves "),
            regex("[A-Z]{2}") sepBy ", ") { _, result -> result },
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
        players: Int
    ): Int {
        val distanceNodes = (openableValves + setOf(node)).toList()
            .map { if (it is MapValvesBackedGraphNode) it else throw IllegalStateException() }
        val distanceMap =
            distanceNodes.map { distanceNodes.indexOf(it) to it.shortestPaths.mapKeys { e -> distanceNodes.indexOf(e.key) } }
                .toMap()
        val distanceArray = distanceMap.filterKeys { it >= 0 }.toList().sortedBy { it.first }
            .map { it.second.filterKeys { it >= 0 }.toList().sortedBy { it.first }.map { it.second }.toIntArray() }
        val countNodes = distanceNodes.size - 1
        val keyMult1 = players + 1
        val keyMult2a = timeLeft + 1
        val keyMult2 = keyMult1 * keyMult2a
        val keyMult3a = countNodes + 1
        val keyMult3 = keyMult2 * keyMult3a
        val cache = IntArray((1 shl keyMult3a) * keyMult2a * (countNodes) * keyMult1) { empty }

        // Due to symmetry, not all possibilities need to be check from the elephant perspective.
        // Thy this is the right number is a little unclear. Might not work for all input.
        val minDepth = countNodes / 2 - 1

        fun go(
            currentTimeLeft: Int,
            currentNode: Int,
            currentOpenableValves: Int,
            player: Int,
            depth: Int
        ): Int {
            val key =
                (keyMult3 * currentOpenableValves) + (keyMult2 * currentNode) + (keyMult1 * currentTimeLeft) + player
            val cached = cache[key]
            val newValves = if (cached != empty) {
                val positive = cached.absoluteValue
                cache[key] = -positive
                positive
            } else {
                val newCache = (0 until countNodes).maxOf { gotoNode ->
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
                            val pressure = go(
                                newTimeLeft,
                                gotoNode,
                                newOpenableValves,
                                player,
                                depth + 1
                            ) + addedPressure
                            pressure
                        }
                    }
                }
                cache[key] = newCache
                newCache
            }
            return if (player > 1 && depth >= minDepth) {
                val newPlayer =
                    go(
                        timeLeft,
                        countNodes,
                        currentOpenableValves,
                        player - 1,
                        depth + 1
                    )
                maxOf(
                    newValves,
                    newPlayer
                )
            } else
                newValves
        }

        return go(timeLeft, countNodes, (1 shl countNodes) - 1, players, 0)
    }
}