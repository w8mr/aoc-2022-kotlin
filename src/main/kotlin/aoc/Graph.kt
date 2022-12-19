package aoc

import java.util.*

interface GraphNode<T> {
    val neighbours: List<Pair<Int, GraphNode<T>>>
    val value: T
}

fun <T> findShortestPaths(node: GraphNode<T>): MutableMap<GraphNode<T>, Pair<Int, GraphNode<T>?>> {
    val shortestPaths = mutableMapOf<GraphNode<T>, Pair<Int, GraphNode<T>?>>()
    val toInspect =
        PriorityQueue<GraphNode<T>> { n1, n2 -> shortestPaths[n1]!!.first - shortestPaths[n2]!!.first }
    val inspected = mutableSetOf<GraphNode<T>>()
    shortestPaths[node] = 0 to null
    toInspect.add(node)
    while (toInspect.isNotEmpty()) {
        val inspect = toInspect.remove()
        val pathLength = shortestPaths[inspect]!!.first
        inspect.neighbours.forEach() { (weight, neighbour) ->
            if (!inspected.contains(neighbour)) {
                val newLength = pathLength + weight
                if (newLength < (shortestPaths[neighbour]?.first ?: Int.MAX_VALUE)) {
                    shortestPaths[neighbour] = newLength to inspect
                    toInspect.remove(neighbour)
                    toInspect.add(neighbour)
                }
            }
        }
        inspected.add(inspect)
    }

    return shortestPaths
}

fun <T> findShortestPath(startNode: GraphNode<T>, endNode: (GraphNode<T>) -> Boolean): Int? {
    val shortestPaths = findShortestPaths(startNode)

    val key = shortestPaths.keys.find(endNode)

    return shortestPaths[key]?.first
}

