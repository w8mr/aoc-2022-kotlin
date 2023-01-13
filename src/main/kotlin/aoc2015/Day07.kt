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

    sealed interface Expression {
        data class Literal(val value: UShort) : Expression
        data class Variable(val name: String) : Expression
        sealed class Opcode() : Expression {
            open class OneArgument(val op: (UShort) -> UShort, open val right: Expression) : Opcode()
            data class Not(override val right: Expression) : OneArgument(Day07::not, right)
            open class TwoArgument(val op: (UShort, UShort) -> UShort, open val left: Expression, open val right: Expression) : Opcode()
            data class And(override val left: Expression, override val right: Expression) : TwoArgument(UShort::and, left, right)
            data class Or(override val left: Expression, override val right: Expression) : TwoArgument(UShort::or, left, right)
            data class LShift(override val left: Expression, override val right: Expression) : TwoArgument(Day07::shiftLeft, left, right)
            data class RShift(override val left: Expression, override val right: Expression) : TwoArgument(Day07::shiftRight, left, right)
        }
    }

    val literal = number map { Expression.Literal(it.toUShort()) }
    val wire = regex("[a-z]+") map(Expression::Variable)
    val argument = literal or wire
    val not = "NOT " and argument map(Expression.Opcode::Not)
    val andOpcode = seq(argument and " AND ", argument, Expression.Opcode::And)
    val orOpcode = seq(argument and " OR ", argument, Expression.Opcode::Or)
    val lShift = seq(argument and " LSHIFT ", argument, Expression.Opcode::LShift)
    val rShift = seq(argument and " RSHIFT ", argument, Expression.Opcode::RShift)
    val opcode = oneOf(andOpcode, orOpcode, lShift, rShift, not)
    val expression = opcode or argument
    val load = seq(expression and " -> ", wire and eol)
    val parser = oneOrMore(load)

    fun solve(parsed: List<Pair<Expression, Expression.Variable>>, wire: String): Int {
        val wires = mutableMapOf<Expression.Variable, UShort>()
        val queue = LinkedList(parsed)

        fun runExpression(opcode: Expression): UShort? =
            when (opcode) {
                is Expression.Literal -> opcode.value
                is Expression.Variable -> wires[opcode]
                is Expression.Opcode.OneArgument -> runExpression(opcode.right)?.let { right ->
                    opcode.op(right)
                }

                is Expression.Opcode.TwoArgument -> runExpression(opcode.left)?.let { left ->
                    runExpression(opcode.right)?.let { right ->
                        opcode.op(left, right)
                    }
                }
            }

        while (queue.isNotEmpty()) {
            val pair = queue.remove()
            val (opcode, wireTo) = pair
            val result = runExpression(opcode)
            when (result) {
                null -> {
                    queue.add(pair)
                }

                else -> {
                    wires[wireTo] = result
                }
            }
        }
        return wires[Expression.Variable(wire)]?.toInt() ?: 0
    }

    fun part1(input: String): Int {
        val parsed = parser(input)

        return solve(parsed, "a")
    }

    fun part2(input: String): Int {
        val parsed = parser(input)

        val a = solve(parsed, "a")
        val changed = parsed.map { if (it.second.name == "b") (Expression.Literal(a.toUShort())) to it.second else it }

        return solve(changed, "a")
    }
}
