package aoc

import kotlin.test.assertEquals

sealed interface RunSafeResult {
    class Success<T>(val value: T) : RunSafeResult
    object NotImplemented : RunSafeResult
    class Exception(val exception: kotlin.Exception) : RunSafeResult

}

private fun <T,R> T.runSafe(code: T.() -> R): RunSafeResult {
    try {
        return RunSafeResult.Success(code())
    } catch (nie: NotImplementedError) {
        return RunSafeResult.NotImplemented
    } catch (e: Exception) {
        return RunSafeResult.Exception(e)
    }
}

private fun RunSafeResult.handle(year: Int, day: Int, part: Int, print: Boolean, expected: Any? = null) {
    when (this) {
        is RunSafeResult.Success<*> -> {
            if (print) println("Year $year day $day part $part: $value")
            if (expected != null) {
                assertEquals(expected, this.value)
            }
        }

        RunSafeResult.NotImplemented -> {
            if (print) println("Year $year day $day part $part: Is not implemented yet")
        }

        is RunSafeResult.Exception -> throw this.exception
    }
}

fun <T, R> T.testSafe(year: Int, day: Int, part: Int, print: Boolean, expected: Any? = null, code: T.() -> R) {
    val result = runSafe(code)
    result.handle(year, day, part, print, expected)
}

