fun main() {
    fun Char.score(): Int =
        when (this) {
            in 'a'..'z' -> this - 'a' + 1
            in 'A'..'Z' -> this - 'A' + 27
            else -> throw IllegalArgumentException("Only a-z and A-Z allowed")
        }

    fun findCommonItemAndScore(input: List<List<List<Char>>>): Int {
        val sets = input.map { it.map(List<Char>::toSet) }
        return sets.map { it.reduce(Set<Char>::intersect).first().score() }.sum()
    }

    fun part1(input: List<List<Char>>): Int =
        findCommonItemAndScore(input.map { it.chunked(it.size / 2) })

    fun part2(input: List<List<Char>>): Int =
        findCommonItemAndScore(input.chunked(3))

    val line = Seq(Regex("[a-zA-Z]*"), Literal("\n")) { l, _ -> l }
    val bag = Map(line, String::toList)
    val parser = ZeroOrMore(bag)

    // test if implementation meets criteria from the description, like:
    val testInput = parser.parse(readFile(2022, 3, 1).readText())
    println(testInput)
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = parser.parse(readFile(2022, 3).readText())
    check(part1(input) == 7863)
    check(part2(input) == 2488)
    println(part1(input))
    println(part2(input))
}

