
class Context(val source: CharSequence, var index: Int = 0) {
    fun error(): Boolean {
        return false
    }

    fun token(value: String, length: Int): Boolean {
        result = value
        index += length
        return true

    }

    var result: Any? = null
}
class Regex(private val pattern: String): Parser<String>() {
    override fun apply(context: Context): Boolean {
        val result = "^$pattern".toRegex().find(context.source, context.index)
        when(result) {
            null -> return context.error()
            else -> return context.token(result.value, result.value.length)
        }
    }
}

class Map<R, T>(private val parser: Parser<R>, private val map: (value: R) -> T): Parser<T>() {
    override fun apply(context: Context): Boolean {
        val result = parser.apply(context)
        if ((result)) {
            context.result = this.map(context.result as R)
            return true
        }
        return false
    }
}

class Sep<R>(private val parser: Parser<R>, private val seperator: Regex): Parser<List<R>>() {
    override fun apply(context: Context): Boolean {
        val list = mutableListOf<R>()
        val result = parser.apply(context)
        list.add(context.result as R)
        while (true) {
            val rsep = seperator.apply(context)
            val rinner = parser.apply(context)
            if (rsep && rinner) {
                list.add(context.result as R)
            } else {
                context.result = list
                return true
            }
        }
        return false
    }
}

class ZeroOrMore<R>(private val parser: Parser<R>): Parser<List<R>>() {
    override fun apply(context: Context): Boolean {
        val list = mutableListOf<R>()
        while (true) {
            val rinner = parser.apply(context)
            if (rinner) {
                list.add(context.result as R)
            } else {
                context.result = list
                return true
            }
        }
        return false
    }
}




class Seq<R1, R2, T>(private val p1: Parser<R1>, private val p2: Parser<R2>, private val map: (v1: R1, v2: R2) -> T): Parser<T>() {
    override fun apply(context: Context): Boolean {
        val r1 = p1.apply(context)
        if ((r1)) {
            val v1 = context.result as R1
            val r2 = p2.apply(context)
            if ((r2)) {
                val v2 = context.result as R2
                context.result = map(v1, v2)
                return true
            }
        }
        return false
    }
}


fun number() = Map(Regex("\\d+")) { it.toInt() }
fun newLine() = Regex("\\r\\n")


abstract class Parser<R> {
    sealed interface Result<R>
    data class Success<R>(val value: R) : Result<R>
    data class Error<R>(val error: String) : Result<R>

    abstract fun apply(context: Context): Boolean

    fun parse(source: CharSequence): R {
        val context = Context(source)
        apply(context)
        return context.result as R
    }


}