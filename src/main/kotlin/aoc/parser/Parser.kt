package aoc.parser

import java.lang.IllegalArgumentException
import java.util.EnumSet
import kotlin.reflect.KClass
import kotlin.reflect.KProperty0

class Context(val source: CharSequence, var index: Int = 0) {
    fun <R> error(message: String = "Unknown"): Parser.Result<R> {
        return Parser.Error(
            "$message at position $index (${
                source.subSequence(
                    index,
                    index + minOf(source.length - index, 15)
                )
            })"
        )
    }

    fun <R> success(value: R, length: Int): Parser.Result<R> {
        index += length
        return Parser.Success(value)

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

    operator fun invoke(source: CharSequence) = parse(source)
}


fun regex(pattern: String) = object: Parser<String>() {
    override fun apply(context: Context): Result<String> {
        if (context.index > context.source.length) return context.error("End of File")
        val result = "^$pattern".toRegex().find(context.source.subSequence(context.index, context.source.length))
        return when (result) {
            null -> context.error("aoc.Regex not matched")
            else -> context.success(result.value, result.value.length)
        }
    }
}

fun <R, T> Parser<R>.to(value: T) =
    this map { value }

infix fun <R, T> Parser<R>.asValue(value: T) = this.to(value)
infix fun <T> String.asValue(value: T) = literal(this) asValue value

inline fun <reified R> Parser<List<R>>.asArray() = this map { it.toTypedArray() }

operator fun <T> Parser<T>.plus(literal: String) =
    seq(this, literal(literal)) { v, _ -> v }

fun literal(literal: String) = object: Parser<Unit>() {
    override fun apply(context: Context): Result<Unit> {
        if (context.index > context.source.length) return context.error("End of File")
        val result = context.source.subSequence(context.index, context.source.length).startsWith(literal)
        return when (result) {
            true -> context.success(Unit, literal.length)
            false -> context.error("$literal not found")
        }
    }
}

infix fun <R,T> Parser<R>.map(map: (value: R) -> T): Parser<T> {
    val parser = this
    return object: Parser<T>() {
        override fun apply(context: Context): Result<T> =
            when (val result = parser.apply(context)) {
                is Success -> context.success(map(result.value), 0)
                is Error -> context.error(result.error)
            }
    }
}

infix fun <R> Parser<R>.sepBy(separator: String) = zeroOrMore(seq(this, optional(literal(separator))) { result, _ -> result})

infix fun <R> Parser<R>.sepBy(separator: Parser<Unit>) = zeroOrMore(seq(this, optional(separator)) { result, _ -> result})

operator fun <R> Parser<R>.times(times: Int) = repeat(this, times, times)

operator fun <R> Int.times(parser: Parser<R>) = repeat(parser, this, this)

operator fun <R> IntRange.times(parser: Parser<R>) = repeat(parser, this.start, this.endInclusive)

fun <R> oneOrMore(parser: Parser<R>): Parser<List<R>> = repeat(parser, min = 1)

fun <R> zeroOrMore(parser: Parser<R>): Parser<List<R>> = repeat(parser)

fun <R> repeat(parser: Parser<R>, max: Int = Int.MAX_VALUE, min: Int = 0) = object: Parser<List<R>>() {
    override fun apply(context: Context): Result<List<R>> {
        val list = mutableListOf<R>()
        val begin = context.index
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
        val size = list.size
        return when {
            size < min -> {
                context.index = begin
                context.error("repeat only $size elements found, needed at least $min")
            }

            size > max -> {
                context.index = begin
                context.error("repeat more elements found, needed at most $max")
            }

            else -> context.success(list, 0)
        }
    }
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

fun <R: Enum<R>> byEnum(e: KClass<R>) =
    byEnum(e) { it -> it.name.lowercase() }

fun <R: Enum<R>> byEnum(e: KClass<R>, f: (R) -> String): Parser<R> {
    val parsers = EnumSet.allOf(e.java).map { literal(f(it!!)).asValue(it) }.toTypedArray()
    return oneOf(*parsers)
}

fun <R: Enum<R>> KClass<R>.asParser(f: (R) -> String) =
    oneOf(*EnumSet.allOf(java).map { literal(f(it)).asValue(it!!) }.toTypedArray())

fun <R: Enum<R>> KClass<R>.asParser() = this.asParser { it.name.lowercase() }


fun <R> oneOf(vararg parsers: Parser<out R>) = object: Parser<R>() {
    override fun apply(context: Context): Result<R> {
        for (parser in parsers) {
            val cur = context.index
            when (val result = parser.apply(context)) {
                is Success -> return context.success(result.value, 0)
                else -> context.index = cur
            }
        }
        return context.error("oneOf has no match")
    }
}

infix fun <L> Parser<out L>.or(other: Parser<out L>): Parser<L> = oneOf(this, other)

sealed interface Either<out L, out R> {
    data class Left<L>(val value: L) : Either<L, Nothing>
    data class Right<R>(val value: R) : Either<Nothing, R>
}

infix fun <L,R> Parser<L>.or_(p2: Parser<R>): Parser<Either<L, R>> {
    val p1 = this
    return object: Parser<Either<L, R>>() {
        override fun apply(context: Context): Result<Either<L, R>> =
            when (val r1 = p1.apply(context)) {
                is Success -> context.success(Either.Left(r1.value), 0)
                is Error ->
                    when (val r2 = p2.apply(context)) {
                        is Success -> context.success(Either.Right(r2.value), 0)
                        is Error -> context.error("aoc.Or both failed")
                    }
            }
    }
}

fun eoF() = object: Parser<Unit>() {
    override fun apply(context: Context): Result<Unit> =
        when  {
            context.index >= context.source.length -> context.success(Unit, 0 )
            else -> context.error("End of file not found")
        }
}

fun empty() = object: Parser<Unit>() {
    override fun apply(context: Context): Result<Unit> =
        context.success(Unit, 0)
}

fun <R> ref(parserRef: KProperty0<Parser<R>>): Parser<R> = object : Parser<R>() {
    override fun apply(context: Context): Result<R> {
        return parserRef.get().apply(context)
    }

}

infix fun <R> String.followedBy(parser: Parser<R>): Parser<R> = seq(literal(this), parser) { _, result -> result}
infix fun <R> Parser<R>.followedBy(literal: String): Parser<R> = seq(this, literal(literal))  { result, _ -> result}
infix fun <R> String.and(parser: Parser<R>): Parser<R> = seq(literal(this), parser) { _, result -> result}
infix fun <R> Parser<R>.and(literal: String): Parser<R> = seq(this, literal(literal))  { result, _ -> result}

@JvmName("followedByUnit")
infix fun <R> Parser<R>.followedBy(parser: Parser<Unit>) = seq(this, parser)  { result, _ -> result}

infix fun <R,T> Parser<R>.followedBy(parser: Parser<T>) = seq(this, parser)
@JvmName("andUnit")
infix fun <R> Parser<R>.and(parser: Parser<Unit>) = seq(this, parser)  { result, _ -> result}

infix fun <R,T> Parser<R>.and(parser: Parser<T>) = seq(this, parser)

fun number() = regex("-?\\d+") map { it.toInt() }
fun digit() = regex("\\d") map { it.toInt() }

fun word() = regex("\\w+")

fun <R> optional(p: Parser<R>) : Parser<R?> =
    p or_ empty() map { o ->
        when (o) {
            is Either.Left -> o.value
            is Either.Right -> null
        }
    }
