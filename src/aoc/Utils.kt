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
