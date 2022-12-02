fun main() {

    fun sumCaloriesByElf(input: List<List<Int>>) =
        input.map { it.sum() }

    fun part1(input: List<List<Int>>): Int {
        return sumCaloriesByElf(input).max()
    }

    fun part2(input: List<List<Int>>): Int {
        return sumCaloriesByElf(input).sorted().reversed().take(3).sum()
    }

    val calories = endNL(number())
    val elf = endNL(ZeroOrMore(calories))
    val elfs = ZeroOrMore(elf)

    // test if implementation meets criteria from the description, like:
    val testInput = elfs.parse(readFile(2022, 1, 1).readText())
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = elfs.parse(readFile(2022, 1).readText())
    check(part1(input) == 67622)
    check(part2(input) == 201491)
    println(part1(input))
    println(part2(input))
}

