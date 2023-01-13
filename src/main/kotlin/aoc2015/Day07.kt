package aoc2015

import aoc.parser.Parsers.eol
import aoc.parser.Parsers.number
import aoc.parser.and
import aoc.parser.map
import aoc.parser.oneOf
import aoc.parser.oneOrMore
import aoc.parser.or
import aoc.parser.regex
import aoc.parser.seq
import java.util.*

class Day07() {
    companion object {
        fun shiftLeft(value: UShort, by: UShort): UShort = (value.toInt() shl by.toInt()).toUShort()
        fun shiftRight(value: UShort, by: UShort): UShort = (value.toInt() shr by.toInt()).toUShort()
        fun load(value: UShort): UShort = value
        fun not(value: UShort): UShort = value.inv()
    }

    sealed interface Opcode {
        open class OneArgument(val op: (UShort) -> UShort, open val right: Argument) : Opcode
        data class Load(override val right: Argument) : OneArgument(Day07::load, right)
        data class Not(override val right: Argument) : OneArgument(Day07::not, right)
        open class TwoArgument(val op: (UShort, UShort) -> UShort, open val left: Argument, open val right: Argument) : Opcode
        data class And(override val left: Argument, override val right: Argument) : TwoArgument(UShort::and, left, right)
        data class Or(override val left: Argument, override val right: Argument) : TwoArgument(UShort::or, left, right)
        data class LShift(override val left: Argument, override val right: Argument) : TwoArgument(Day07::shiftLeft, left, right)
        data class RShift(override val left: Argument, override val right: Argument) : TwoArgument(Day07::shiftRight, left, right)
    }

    sealed interface Argument {
        data class Literal(val value: UShort) : Argument
        data class Wire(val name: String) : Argument
    }

    val literal = number map { Argument.Literal(it.toUShort()) }
    val wire = regex("[a-z]+") map(Argument::Wire)
    val argument = literal or wire
    val load = argument map(Opcode::Load)
    val not = "NOT " and argument map(Opcode::Not)
    val andOpcode = seq(argument and " AND ", argument, Opcode::And)
    val orOpcode = seq(argument and " OR ", argument, Opcode::Or)
    val lShift = seq(argument and " LSHIFT ", argument, Opcode::LShift)
    val rShift = seq(argument and " RSHIFT ", argument, Opcode::RShift)
    val opcode = oneOf(andOpcode, orOpcode, lShift, rShift, not, load)
    val instruction = seq(opcode and " -> ", wire and eol)
    val parser = oneOrMore(instruction)

    fun solve(parsed: List<Pair<Opcode, Argument.Wire>>, wire: String): Int {
        val wires = mutableMapOf<Argument.Wire, UShort>()
        val queue = LinkedList(parsed)

        fun hasValue(argument: Argument) =
            when (argument) {
                is Argument.Literal -> argument.value
                is Argument.Wire -> wires[argument]
            }

        fun run(opcode: Opcode) =
            when (opcode) {
                is Opcode.OneArgument -> hasValue(opcode.right)?.let { right ->
                    opcode.op(right)
                }

                is Opcode.TwoArgument -> hasValue(opcode.left)?.let { left ->
                    hasValue(opcode.right)?.let { right ->
                        opcode.op(left, right)
                    }
                }
            }

        while (queue.isNotEmpty()) {
            val pair = queue.remove()
            val (opcode, wireTo) = pair
            val result = run(opcode)
            when (result) {
                null -> {
                    queue.add(pair)
                }

                else -> {
                    wires[wireTo] = result
                }
            }
        }
        return wires[Argument.Wire(wire)]?.toInt() ?: 0
    }

    fun part1(input: String): Int {
        val parsed = parser(input)

        return solve(parsed, "a")
    }

    fun part2(input: String): Int {
        val parsed = parser(input)

        val a = solve(parsed, "a")
        val changed = parsed.map { if (it.second.name == "b") (Opcode.Load(Argument.Literal(a.toUShort()))) to it.second else it }

        return solve(changed, "a")
    }
}
