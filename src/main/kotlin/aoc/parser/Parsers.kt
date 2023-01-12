package aoc.parser

object Parsers {
    val number = number()
    val word = word()

    val eol = literal("\n")
}