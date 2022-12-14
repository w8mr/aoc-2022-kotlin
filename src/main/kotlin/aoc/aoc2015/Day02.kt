package aoc.aoc2015

import aoc.*

data class Box(val l: Int, val w: Int, val h: Int) {
    fun wrappingPaper(): Int {
        return 2*(l*w+l*h+w*h)+Math.min(Math.min(l*w, l*h), h*w)
    }
    fun ribbonLint(): Int {
        return 2*Math.min(Math.min(l+w, l+h), h+w)+l*w*h
    }
}

fun main() {
    val box = seq(
        number()+"x",
        number()+"x",
        number()+"\n")
    { l, w, h -> Box(l,w,h) }
    val boxes = zeroOrMore(box)

    fun part1(input: String): Int {
        return boxes.parse(input).map(Box::wrappingPaper).sum()
    }

    fun part2(input: String): Int {
        return boxes.parse(input).map(Box::ribbonLint).sum()
    }

    check(part1("2x3x4\n") == 58)
    check(part1("1x1x10\n") == 43)

    val input = readFile(2015, 2).readText()
    check(part1(input) == 1588178)
    println(part1(input))

    check(part2("2x3x4\n") == 34)
    check(part2("1x1x10\n") == 14)

    check(part2(input) == 3783758)
    println(part2(input))

}

