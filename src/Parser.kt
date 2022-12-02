import java.lang.IllegalArgumentException

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
            null -> context.error("Regex not matched")
            else -> context.success(result.value, result.value.length)
        }
    }
}

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

//class Sep<R>(private val parser: Parser<R>, private val seperator: Regex): Parser<List<R>>() {
//    override fun apply(context: Context): Boolean {
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

class ZeroOrMore<R>(private val parser: Parser<R>): Parser<List<R>>() {
    override fun apply(context: Context): Result<List<R>> {
        val list = mutableListOf<R>()
        while (context.index < context.source.length) {
            when (val result = parser.apply(context)) {
                is Success -> list.add(result.value)
                else -> break
            }
        }
        return context.success(list, 0)    }
}




class Seq<R1, R2, T>(private val p1: Parser<R1>, private val p2: Parser<R2>, private val map: (v1: R1, v2: R2) -> T): Parser<T>() {
    override fun apply(context: Context): Result<T> =
        when (val r1 = p1.apply(context)) {
            is Success ->
                when (val r2 = p2.apply(context)) {
                    is Success -> context.success(map(r1.value, r2.value), 0)
                    is Error -> context.error("Sequence second parser failed")
                }
            is Error -> context.error("Sequence first parser failed")
        }
}

class Seq3<R1, R2, R3, T>(private val p1: Parser<R1>, private val p2: Parser<R2>, private val p3: Parser<R3>, private val map: (v1: R1, v2: R2, v3: R3) -> T): Parser<T>() {
    override fun apply(context: Context): Result<T> =
        when (val r1 = p1.apply(context)) {
            is Success ->
                when (val r2 = p2.apply(context)) {
                    is Success ->
                        when (val r3 = p3.apply(context)) {
                            is Success -> context.success(map(r1.value, r2.value, r3.value), 0)
                            is Error -> context.error("Sequence third parser failed")
                        }
                    is Error -> context.error("Sequence second parser failed")
                }
            is Error -> context.error("Sequence first parser failed")
        }
}

class Seq4<R1, R2, R3, R4, T>(private val p1: Parser<R1>, private val p2: Parser<R2>, private val p3: Parser<R3>, private val p4: Parser<R4>, private val map: (v1: R1, v2: R2, v3: R3, v4: R4) -> T): Parser<T>() {
    override fun apply(context: Context): Result<T> =
        when (val r1 = p1.apply(context)) {
            is Success ->
                when (val r2 = p2.apply(context)) {
                    is Success ->
                        when (val r3 = p3.apply(context)) {
                            is Success ->
                                when (val r4 = p4.apply(context)) {
                                    is Success -> context.success(map(r1.value, r2.value, r3.value, r4.value), 0)
                                    is Error -> context.error("Sequence forth parser failed")
                                }
                            is Error -> context.error("Sequence third parser failed")
                        }
                    is Error -> context.error("Sequence second parser failed")
                }
            is Error -> context.error("Sequence first parser failed")
        }
}


sealed interface OrResult<out L, out R> {
    data class Left<L>(val left: L) : OrResult<L, Nothing>
    data class Right<R>(val right: R) : OrResult<Nothing, R>
}

class OneOf<R>(private vararg val parsers: Parser<R>): Parser<R>() {
    override fun apply(context: Context): Result<R> {
        for (parser in parsers) {
            when (val result = parser.apply(context)) {
                is Success -> return context.success(result.value, 0)
                else -> {}
            }
        }
        return context.error("OneOf has no match")
    }
}

class Or<L,R>(private val p1: Parser<L>, private val p2: Parser<R>): Parser<OrResult<L,R>>() {
    override fun apply(context: Context): Result<OrResult<L, R>> =
        when (val r1 = p1.apply(context)) {
            is Success -> context.success(OrResult.Left(r1.value), 0)
            is Error ->
                when (val r2 = p2.apply(context)) {
                    is Success -> context.success(OrResult.Right(r2.value), 0)
                    is Error -> context.error("Or both failed")
                }
        }
}

class EoF(): Parser<Unit>() {
    override fun apply(context: Context): Result<Unit> =
        when  {
            context.index >= context.source.length -> context.success(Unit, 0 )
            else -> context.error("End of file not found")
        }
}


fun number() = Map(Regex("\\d+")) { it.toInt() }

fun <R> endNLorEoF(parser: Parser<R>): Parser<R> = Seq(parser, Or(Literal("\n"), EoF())) { value, _ -> value }

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