import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * Splits a list by the given predicate
 *
 * @param predicate function that takes a element of the list and return true when predicate is matched
 *
 * @return List of List with a split parts
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
