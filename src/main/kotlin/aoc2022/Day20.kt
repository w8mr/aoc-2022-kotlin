package aoc2022

import aoc.*

class Day20() {
    val parser = zeroOrMore(number() followedBy "\n")

    fun part1(input: String): Int {
        val parsed = parser.parse(input)
        val size = parsed.size
        val sizem1 = size -1
        //println(size)
        //println(parsed)

        val indexes = List(size) { it }.toMutableList()

        parsed.forEachIndexed {idx, move ->
            val from = indexes.indexOf(idx)
            val to = (from + move).mod(sizem1)
            val e = indexes.removeAt(from)
            indexes.add(to, e)

            //val list = indexes.map { parsed[it] }
            //println(list)
        }
        val list = indexes.map { parsed[it] }


        val indexOf0 = list.indexOf(0)

        return listOf(1000,2000,3000).sumOf {
            list[(indexOf0 + it).mod(size)] }
    }

    fun part2(input: String): Int {
        return TODO()
    }
}

/*

      A      B      C
p0         p1000

A: zone between pointer 0 and pointer 1000
B: zone is pointer 1000
C: zone betweeen pointer 1000 and pointer 0

p1000 is always to the right of p0 even if index is lower.

p1000 will be changed by move element from
        A   B   C
to  A   0   ?   1
    B   ?   0   ?
    C  -1   ?   0

*/
/*

Initial arrangement:
1, 2, -3, 3, -2, 0, 4
step 0
removeIdx = 0
insertIdx = 1

1 moves between 2 and -3:
2, 1, -3, 3, -2, 0, 4
step 1
removeIdx = 0
insertIndex = 2

2 moves between -3 and 3:
1, -3, 2, 3, -2, 0, 4
step2
removeIdx = 1
insertInx = -2 -> 5

-3 moves between -2 and 0:
1, 2, 3, -2, -3, 0, 4
removeIdx = 2
insertIdx = 5

3 moves between 0 and 4:
1, 2, -2, -3, 0, 3, 4

-2 moves between 4 and 1:
1, 2, -3, 0, 3, 4, -2

0 does not move:
1, 2, -3, 0, 3, 4, -2

4 moves between -3 and 0:
1, 2, -3, 4, 0, 3, -2
 */