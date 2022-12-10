package aoc

import aoc2022.Noop
import java.lang.IllegalArgumentException
import kotlin.reflect.KProperty0

class Context(val source: CharSequence, var index: Int = 0) {
    fun <R> error(message: String = "Unknown"): Parser.Result<R> {
        return Parser.Error(message)
    }

    fun <R> success(value: R, length: Int): Parser.Result<R> {
        index += length
        return Parser.Success(value)

    }
}
class Regex(private val pattern: String): Parser<String>() {
    override fun apply(context: Context): Result<String> {
        if (context.index > context.source.length) return context.error("End of File")
        val result = "^$pattern".toRegex().find(context.source.subSequence(context.index, context.source.length))
        return when (result) {
            null -> context.error("aoc.Regex not matched")
            else -> context.success(result.value, result.value.length)
        }
    }
}

abstract class Parser<R> {
    sealed interface Result<R>
    data class Success<R>(val value: R) : Result<R>
    data class Error<R>(val error: String) : Result<R>

    abstract fun apply(context: Context): Result<R>

    fun parse(source: CharSequence): R =
        when (val result = apply(Context(source))) {
            is Success -> result.value
            is Error -> throw IllegalArgumentException(result.error)
        }
}

fun <R, T> Parser<R>.to(value: T) =
    Map(this) { value }

infix fun <R, T> Parser<R>.asValue(value: T) = this.to(value)
infix fun <T> String.asValue(value: T) = Literal(this) asValue value

operator fun <T> Parser<T>.plus(literal: String) =
    seq(this, Literal(literal)) { v, _ -> v }

//operator fun <T> String.plus(parser: Parser<T>) =
//    seq(Literal(this), parser) { _, v -> v }

class Literal(private val literal: String): Parser<String>() {
    override fun apply(context: Context): Result<String> {
        if (context.index > context.source.length) return context.error("End of File")
        val result = context.source.subSequence(context.index, context.source.length).startsWith(literal)
        return when (result) {
            true -> context.success(literal, literal.length)
            false -> context.error("$literal not found")
        }
    }
}

class Map<R, T>(private val parser: Parser<R>, private val map: (value: R) -> T): Parser<T>() {
    override fun apply(context: Context): Result<T> =
        when (val result = parser.apply(context)) {
            is Success -> context.success(this.map(result.value), 0)
            is Error -> context.error(result.error)
        }
}

infix fun <R,T> Parser<R>.map(map: (value: R) -> T): Parser<T> {
    val parser = this
    return object: Parser<T>() {
        override fun apply(context: Context): Parser.Result<T> =
            when (val result = parser.apply(context)) {
                is Parser.Success -> context.success(map(result.value), 0)
                is Parser.Error -> context.error(result.error)
            }
    }
}

//class Sep<R>(private val parser: aoc.Parser<R>, private val seperator: aoc.Regex): aoc.Parser<List<R>>() {
//    override fun apply(context: aoc.Context): Boolean {
//        val list = mutableListOf<R>()
//        val result = parser.apply(context)
//        list.add(context.result as R)
//        while (true) {
//            val rsep = seperator.apply(context)
//            val rinner = parser.apply(context)
//            if (rsep && rinner) {
//                list.add(context.result as R)
//            } else {
//                context.result = list
//                return true
//            }
//        }
//        return false
//    }
//}

fun <R> zeroOrMore(parser: Parser<R>) = object: Parser<List<R>>() {
    override fun apply(context: Context): Result<List<R>> {
        val list = mutableListOf<R>()
        while (context.index < context.source.length) {
            val cur = context.index
            when (val result = parser.apply(context)) {
                is Success -> list.add(result.value)
                else -> {
                    context.index = cur
                    break
                }
            }
        }
        return context.success(list, 0)    }
}

fun <R1, R2, T> seq(p1: Parser<R1>, p2: Parser<R2>, map: (v1: R1, v2: R2) -> T) = object: Parser<T>() {
    override fun apply(context: Context): Result<T> =
        when (val r1 = p1.apply(context)) {
            is Success ->
                when (val r2 = p2.apply(context)) {
                    is Success -> context.success(map(r1.value, r2.value), 0)
                    is Error -> context.error("seq second failed: ${r2.error}")
                }
            is Error -> context.error("seq first failed: ${r1.error}")
        }
}

fun <R1, R2> seq(p1: Parser<R1>, p2: Parser<R2>) =
    seq(p1,p2) { v1, v2 -> Pair(v1, v2) }

fun <R1, R2, R3, T> seq(p1: Parser<R1>, p2: Parser<R2>, p3: Parser<R3>, map: (v1: R1, v2: R2, v3: R3) -> T): Parser<T> {
    return seq(seq(p1, p2), p3) { (v1, v2), v3 -> map(v1, v2, v3) }
}

fun <R1, R2, R3, R4, T> seq(p1: Parser<R1>, p2: Parser<R2>, p3: Parser<R3>, p4: Parser<R4>, map: (v1: R1, v2: R2, v3: R3, v4: R4) -> T): Parser<T> {
    return seq(seq(p1, p2), seq(p3, p4)) { (v1, v2), (v3, v4) -> map(v1, v2, v3, v4) }
}

fun <R1, R2, R3, R4, R5, T> seq(p1: Parser<R1>, p2: Parser<R2>, p3: Parser<R3>, p4: Parser<R4>, p5: Parser<R5>, map: (v1: R1, v2: R2, v3: R3, v4: R4, v5: R5) -> T): Parser<T> {
    return seq(seq(p1, p2), seq(p3, p4), p5) { (v1, v2), (v3, v4), v5 -> map(v1, v2, v3, v4, v5) }
}

fun <R1, R2, R3, R4, R5, R6, T> seq(p1: Parser<R1>, p2: Parser<R2>, p3: Parser<R3>, p4: Parser<R4>, p5: Parser<R5>, p6: Parser<R6>, map: (v1: R1, v2: R2, v3: R3, v4: R4, v5: R5, v6: R6) -> T): Parser<T> {
    return seq(seq(p1, p2), seq(p3, p4), seq(p5, p6) { v5, v6 -> Pair(v5, v6) }) { (v1, v2), (v3, v4), (v5, v6) -> map(v1, v2, v3, v4, v5, v6) }
}

sealed interface OrResult<out L, out R> {
    data class Left<L>(val value: L) : OrResult<L, Nothing>
    data class Right<R>(val value: R) : OrResult<Nothing, R>
}

class OneOf<R>(private vararg val parsers: Parser<out R>): Parser<R>() {
    override fun apply(context: Context): Result<R> {
        for (parser in parsers) {
            when (val result = parser.apply(context)) {
                is Success -> return context.success(result.value, 0)
                else -> {}
            }
        }
        return context.error("aoc.OneOf has no match")
    }
}

class Or<L,R>(private val p1: Parser<L>, private val p2: Parser<R>): Parser<OrResult<L, R>>() {
    override fun apply(context: Context): Result<OrResult<L, R>> =
        when (val r1 = p1.apply(context)) {
            is Success -> context.success(OrResult.Left(r1.value), 0)
            is Error ->
                when (val r2 = p2.apply(context)) {
                    is Success -> context.success(OrResult.Right(r2.value), 0)
                    is Error -> context.error("aoc.Or both failed")
                }
        }
}

infix fun <L,R> Parser<L>.orLR(other: Parser<R>): Parser<OrResult<L, R>> = Or(this, other)

@JvmName("or")
infix fun <L> Parser<out L>.or(other: Parser<out L>): Parser<L> = OneOf(this, other)

class EoF(): Parser<Unit>() {
    override fun apply(context: Context): Result<Unit> =
        when  {
            context.index >= context.source.length -> context.success(Unit, 0 )
            else -> context.error("End of file not found")
        }
}

class Empty(): Parser<Unit>() {
    override fun apply(context: Context): Result<Unit> =
        context.success(Unit, 0)
}

fun <R> ref(parserRef: KProperty0<Parser<R>>): Parser<R> = object : Parser<R>() {
    override fun apply(context: Context): Result<R> {
        return parserRef.get().apply(context)
    }

}


fun number() = Map(Regex("-?\\d+")) { it.toInt() }
fun digit() = Map(Regex("\\d")) { it.toInt() }

fun <R> optional(p: Parser<R>) : Parser<R?> =
    Map(Or(p, Empty())) { o ->
        when (o) {
            is OrResult.Left -> o.value
            is OrResult.Right -> null
        }
    }
fun <R> endNLorEoF(parser: Parser<R>): Parser<R> = seq(parser, Or(Literal("\n"), EoF())) { value, _ -> value }

