package aoc2022

import aoc.*

class Day21() {
    enum class Operation(val text: String, val function: (Double, Double) -> Double){
        PLUS("+", Double::plus),
        MINUS("-", Double::minus),
        TIMES("*", Double::times),
        DIV("/", Double::div)
    }

    sealed interface Monkey{
        val name: String
    }
    data class NumberMonkey(override val name: String, val number: Double) : Monkey
    data class OperationMonkey(override val name: String, val monkey1: String, val operation: Operation, val monkey2: String) : Monkey


    val operation = byEnum(Operation::class, Operation::text)
    val numberMonkey = seq(word() followedBy ": ", number() map { it.toDouble()}, ::NumberMonkey)
    val operationMonkey = seq(word() followedBy ": ", word() followedBy " ", operation followedBy " ", word(),  ::OperationMonkey)
    val monkey = numberMonkey or operationMonkey followedBy "\n"
    val parser = zeroOrMore(monkey)// map { it.associateBy(Monkey::name)}

    private fun solve(
        parsed: List<Monkey>, human: Double? = null
    ): MutableMap<String, NumberMonkey> {
        val (known, unknown) = parsed.partition { it is NumberMonkey }
        val knownMap = known.associateBy(Monkey::name).mapValues { it.value as NumberMonkey }.toMutableMap()
        if (human!=null) knownMap["humn"] = NumberMonkey("humn", human)
        val unknownMut = unknown.map { it as OperationMonkey }.toMutableList()
        while (unknownMut.isNotEmpty()) {
            val removable = mutableListOf<OperationMonkey>()
            unknownMut.forEach { monkey ->
                if ((monkey.monkey1 in knownMap) && (monkey.monkey2 in knownMap)) {
                    val number1 = knownMap.getValue(monkey.monkey1).number
                    val number2 = knownMap.getValue(monkey.monkey2).number
                    val number = monkey.operation.function(number1, number2)
                    removable.add(monkey)
                    knownMap.put(monkey.name, NumberMonkey(monkey.name, number))
                }
            }
            unknownMut.removeAll(removable)
        }
   //     println(knownMap)
        return knownMap
    }

    fun part1(input: String): Long {
        val parsed = parser.parse(input)
        val knownMap = solve(parsed)
        return knownMap.getValue("root").number.toLong()
    }

    fun part2(input: String): Long {
        val parsed = parser.parse(input)
        val root = parsed.find { it.name == "root" }!!  as OperationMonkey
        val monkeySearch = (root).monkey1

        val searchKnownMap = solve(parsed)
        val search = searchKnownMap.getValue((root).monkey2).number
      //  println(search)

        var low = 0.0
        var high = searchKnownMap.getValue("humn").number

        fun test(test: Double) = solve(parsed, test).getValue(monkeySearch).number - search

        var diff = test(high)
        while(diff>=0) {
       //     println("$diff $low $high")
            low = high
            high = high * 2
            diff = test(high)
        }
        while(true) {
         //   println("$diff $low $high")

            val mid = (low + high) / 2
            diff = test(mid)
            when {
                diff > 0L -> low = mid
                diff < 0L -> high = mid
                else -> return mid.toLong()
            }
        }
    }

}

/*
prrg=NumberMonkey(name=prrg, number=59078404922637)
jntz=NumberMonkey(name=jntz, number=28379346560301)
 */

