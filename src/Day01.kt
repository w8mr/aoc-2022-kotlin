fun main() {

    fun sumCaloriesByElf(input: List<String>) =
        input.split(String::isEmpty).map { it.sumOf(String::toInt) }

    fun part1(input: List<String>): Int {
        return sumCaloriesByElf(input).max()
    }

    fun part2(input: List<String>): Int {
        return sumCaloriesByElf(input).sorted().reversed().take(3).sum()
    }

  //  val line = Seq(number(), newLine()) { n, _ -> n }
  //  val elf = ZeroOrMore(line)
  //  val test = elf.parse("123\r\n345\r\n687\r\n\r\n321\r\n")
  //  println(test)

    // test if implementation meets criteria from the description, like:
    val testInput = readFile(2022, 1, 1).readLines()
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readFile(2022,1).readLines()
    check(part1(input) == 67622)
    check(part2(input) == 201491)
    println(part1(input))
    println(part2(input))
}

