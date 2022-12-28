package aoc

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

fun readFile(year: Int, day: Int, test: Int? = null): File {
    fun Int.twoDigit() = String.format("%02d", this)
    val testSuffix = if (test != null) "_test${test.twoDigit()}" else ""
    val basePath = if (test != null) "input/test" else "input/actual"
    return File("$basePath/$year/Day${day.twoDigit()}$testSuffix.txt")
}

/**
 * Converts string to aoc.md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * Splits a list by the given predicate
 *
 * @param predicate function that takes a element of the list and return true when predicate is matched
 *
 * @return List of List with a aoc.split parts
 */
fun <A> List<A>.split(predicate: (A) -> Boolean): List<List<A>> {
    fun go(input: List<A>): List<List<A>> {
        val first = input.takeWhile { !predicate(it) }
        val other = input.dropWhile { !predicate(it) }.drop(1)
        when {
            other.isEmpty() -> return listOf(first)
            else -> return listOf(first) + go(other)
        }
    }
    return go(this)
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
    // Helpers
    fun <T> List<T>.tail(): List<T> = this.takeLast(this.size - 1)
    fun <T> T.append(xs: List<T>): List<T> = listOf(this).plus(xs)

    this.filter { it.isNotEmpty() }.let { ys ->
        return when (ys.isNotEmpty()) {
            true -> ys.map { it.first() }.append(ys.map { it.tail() }.transpose())
            else -> emptyList()
        }
    }
}

fun <R, T> foldTree(tree: R, subNodes: (R) -> List<R>, init: T, f: (T, R) -> T): T {
    fun go(node: R, acc: T, f: (T, R) -> T): T {
        val nacc = f(acc, node)
        val subs = subNodes(node)
        return subs.fold(nacc) { a, n -> go(n, a, f) }
    }
    return go(tree, init, f)
}

fun IntRange.union(other: IntRange) = union(listOf(this, other))

fun union(r: List<IntRange>): List<IntRange> {
    val sorted = r.sortedBy { it.start }
    val merged =
        sorted.fold(Pair(listOf<IntRange>(), sorted[0])) { (list: List<IntRange>, cur: IntRange), range: IntRange ->
            when (range.first) {
                in cur -> Pair(list, cur.first..maxOf(range.last, cur.last))
                else -> Pair(list + listOf(cur), range)
            }
        }
    return merged.first + listOf(merged.second)
}



fun <T> getCombinationPairs(list: List<T>, n: Int): List<Pair<List<T>, List<T>>> {
    val m = list.size
    val result = mutableListOf<Pair<List<T>, List<T>>>()

    fun go(n: Int, indexes: IntArray, start: Int, index: Int) {
        for (i in start..(m-1)) {
            indexes[index] = i
            //         println("indexes: ${indexes.toList()}, start: ${start}, index: ${index}")
            val newIndex = index + 1
            if (newIndex == n) {
                val pair1 =
                    Pair(list.filterIndexed { it, _ -> it in indexes }, list.filterIndexed { it, _ -> it !in indexes })
                //           println("added: $pair1")
                result.add(pair1)
            } else {
                go(n, indexes, i+1, newIndex)
            }
        }
    }
    for (j in 1..n) {
        go(j, IntArray(j), 0, 0)

    }
    return result

}

fun <T> getCombinations(list: Collection<T>): List<List<T>> =
    getCombinations(list, list.size)

fun <T> getCombinations(list: Collection<T>, n: Int): List<List<T>> {
    val m = list.size
    val result = mutableListOf<List<T>>()

    fun go(n: Int, indexes: IntArray, start: Int, index: Int) {
        for (i in start..(m-1)) {
            indexes[index] = i
            //         println("indexes: ${indexes.toList()}, start: ${start}, index: ${index}")
            val newIndex = index + 1
            if (newIndex == n) {
                val pair1 =
                    list.filterIndexed { it, _ -> it in indexes }
                //           println("added: $pair1")
                result.add(pair1)
            } else {
                go(n, indexes, i+1, newIndex)
            }
        }
    }
    for (j in 1..n) {
        go(j, IntArray(j), 0, 0)

    }
    return result

}






