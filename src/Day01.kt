fun main() {

    fun sumCaloriesByElf(input: List<String>) =
        input.split(String::isEmpty).map { it.sumOf(String::toInt) }

    fun part1(input: List<String>): Int {
        return sumCaloriesByElf(input).max()
    }

    fun part2(input: List<String>): Int {
        return sumCaloriesByElf(input).sorted().reversed().take(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    check(part1(input) == 67622)
    check(part2(input) == 201491)
    println(part1(input))
    println(part2(input))
}
