package aoc2022

import aoc.*

class Day25 {
    val SnafuDigits = "=-012"

    data class Snafu(val number: String) {
        val SnafuDigits = "=-012"

        init {
            require(number.all { it in SnafuDigits}) { "number is not valid SNAFU" }
        }


        fun toLong() =
            this.number.reversed().fold(Pair(0L, 1L)) { (acc, m), c ->
                Pair(acc + m * (SnafuDigits.indexOf(c) - 2), m * 5)
            }.first
    }

    fun Long.toSnafu() = Snafu(sequence {
        var number = this@toSnafu
        while (number > 0) {
            val digit = number % 5
            when {
                digit > 2 -> {
                    number += 5
                    number /= 5
                    yield(SnafuDigits[digit.toInt() - 3])
                }
                else -> {
                    number /= 5
                    yield(SnafuDigits[digit.toInt() + 2])
                }
            }
        }
    }.joinToString("").reversed())

    fun part1(input: String): String {
        val parser = oneOrMore(regex("[012=-]+") map (::Snafu) followedBy "\n")
        val parsed = parser.parse(input)
        val sum = parsed.map(Snafu::toLong).sum()


        return sum.toSnafu().number
    }

}

