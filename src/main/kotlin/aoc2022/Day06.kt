package aoc2022

class Day06() {
    fun solve(input: String, n: Int) =
        n + input
            .windowed(n)
            .map { it.toSet().size == n }
            .indexOfFirst { it }

    fun part1(input: String): Int {
        return solve(input, 4)
    }

    fun part2(input: String): Int {
        return solve(input, 14)
    }
}

