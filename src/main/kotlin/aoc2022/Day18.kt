package aoc2022

import aoc.*
import java.util.*

typealias CubeSet = MutableMap<Int, MutableMap<Int, MutableSet<Int>>>

class Day18() {
    data class Cube(val x: Int, val y: Int, val z: Int)

    val cube = seq(number() followedBy ",", number() followedBy ",", number() followedBy "\n", ::Cube)
    val parser = zeroOrMore(cube)

    private fun cubesMapOf(parsed: Iterable<Cube>): CubeSet =
        parsed.fold(mutableMapOf()) { acc, e -> acc.add(e); acc}

    private fun CubeSet.add(
        cube: Cube
    ) {
        val yMap = getOrPut(cube.x) { -> mutableMapOf() }
        val zSet = yMap.getOrPut(cube.y) { -> mutableSetOf() }
        zSet.add(cube.z)
    }

    sealed class Dir3(val x:Int, val y:Int, val z:Int) {
        object Left: Dir3(-1, 0, 0)
        object Right: Dir3(1, 0, 0)
        object Back: Dir3(0, -1, 0)
        object Front: Dir3(0, 1, 0)
        object Down: Dir3(0, 0, -1)
        object Up: Dir3(0, 0, 1)
    }

    val dirs3 = listOf(Dir3.Left, Dir3.Right, Dir3.Back, Dir3.Front, Dir3.Down, Dir3.Up)

    operator fun Cube.plus(dir: Dir3) =
        Cube(this.x + dir.x, this.y + dir.y, this.z + dir.z)

    operator fun CubeSet.contains(cube: Cube) =
        this.contains(cube.x, cube.y, cube.z)

    fun CubeSet.contains(x:Int, y: Int, z: Int) =
        this[x]?.get(y)?.contains(z) == true

    private fun surfaceArea(
        parsed: List<Cube>,
        cubes: CubeSet
    ) = parsed.sumOf { cube ->
        dirs3.count { dir ->
            (cube + dir) !in cubes
        }
    }

    fun part1(input: String): Int {
        val parsed = parser.parse(input)
        val cubes = cubesMapOf(parsed)
        val result = surfaceArea(parsed, cubes)
        return result
    }

    private fun surfaceAreaExterior(
        parsed: List<Cube>,
        exterior: Set<Cube>
    ) = parsed.sumOf { cube ->
        dirs3.count { dir ->
            (cube + dir) in exterior
        }
    }

    fun part2(input: String): Int {
        val parsed = parser.parse(input)
        val cubes = cubesMapOf(parsed)
        val minX = parsed.minOfOrNull(Cube::x)!! - 1
        val maxX = parsed.maxOfOrNull(Cube::x)!! + 1
        val minY = parsed.minOfOrNull(Cube::y)!! - 1
        val maxY = parsed.maxOfOrNull(Cube::y)!! + 1
        val minZ = parsed.minOfOrNull(Cube::z)!! - 1
        val maxZ = parsed.maxOfOrNull(Cube::z)!! + 1

        val queue: Queue<Cube> = LinkedList()
        val exterior = mutableSetOf<Cube>()

        val start = Cube(minX, minY, minZ)
        assert(start !in cubes)

        queue.add(start)
        while(queue.isNotEmpty()) {
            val current = queue.remove()
            exterior.add(current)
            dirs3.forEach { dir ->
                val newCube = current + dir
                if ((newCube.x in minX..maxX) &&
                    (newCube.y in minY..maxY) &&
                    (newCube.z in minZ..maxZ) &&
                    (newCube !in exterior) &&
                    (newCube !in cubes)) {
                    exterior.add(newCube)
                    queue.add(newCube)
                }
            }
        }
        return surfaceAreaExterior(parsed, exterior)
    }
}

