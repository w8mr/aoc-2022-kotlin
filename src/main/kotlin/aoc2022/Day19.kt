package aoc2022

import aoc.*
import org.omg.CORBA.MARSHAL
import kotlin.math.cos

class Day19() {
    //Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
    enum class Material {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE
    }

    data class Cost(val amount: Int, val material: Material)
    data class BuildInstruction(val type: Material, val costs: List<Cost>)
    data class Blueprint(val index: Int, val buildInstruction: Map<Material, BuildInstruction>)

    val material = byEnum(Material::class)
    val index = "Blueprint " followedBy number() followedBy ":"
    val cost = seq(number(), " " followedBy material, ::Cost)
    val costs = cost sepBy " and "
    val build = seq(" Each " followedBy material, " robot costs " followedBy costs, ::BuildInstruction) followedBy "."
    val builds = zeroOrMore(build) map { it.associateBy(BuildInstruction::type) }
    val line = seq(index, builds, ::Blueprint) followedBy "\n"
    val parser = zeroOrMore(line)

    fun Blueprint.score(timeLeft: Int, robots: Map<Material, Int>, materials: Map<Material, Int>, max: Map<Material, Int>): Int {
        if (timeLeft == 0) {
            println("materials: $materials, robots: $robots")
            return materials[Material.GEODE]?:0
        }

        val newMaterials: Map<Material, Int> = mineMaterials(materials, robots)
        val buildableRobots = this.buildInstruction.filterValues { instr ->
            instr.costs.all { cost ->
                materials[cost.material]?: 0 >= cost.amount
            }
        }.filterKeys { robots[it]?:0 < max[it]?:0 }
        if (buildableRobots.isNotEmpty()) {
            return maxOf(
                score(timeLeft - 1, robots, newMaterials, max),
                buildableRobots.maxOf { buildRobot ->
                    val (newRobots, newMaterials2) = buildRobots(robots, buildRobot, newMaterials)
                    score(timeLeft - 1, newRobots, newMaterials2, max)
                }
            )

        } else {
            return score(timeLeft - 1, robots, newMaterials, max)
        }
    }

    private fun Blueprint.buildRobots(
        robots: Map<Material, Int>,
        buildRobot: Map.Entry<Material, BuildInstruction>,
        newMaterials: Map<Material, Int>
    ): Pair<Map<Material, Int>, Map<Material, Int>> {
        val newRobots: MutableMap<Material, Int> = HashMap(robots)
        newRobots.merge(buildRobot.key, 1) { old, _ -> old + 1 }
        val newMaterials = HashMap(newMaterials)
        this.buildInstruction[buildRobot.key]!!.costs.forEach { cost ->
            newMaterials.merge(cost.material, 0) { old, _ -> old - cost.amount }
        }
        return Pair(newRobots, newMaterials)
    }

    private fun mineMaterials(
        materials: Map<Material, Int>,
        robots: Map<Material, Int>
    ): Map<Material, Int> {
        val newMaterials: MutableMap<Material, Int> = HashMap(materials)
        robots.forEach { robot ->
            newMaterials.merge(robot.key, 1) { old, _ -> old + 1 }
        }
        return newMaterials
    }

    fun part1(input: String): Int {
        val parsed = parser.parse(input)
        val max = mapOf(Material.ORE to 1, Material.CLAY to 4, Material.OBSIDIAN to 2, Material.GEODE to 1)
        println(parsed[0].score(24, mapOf(Material.ORE to 1), mapOf(), max))
        //return parsed.sumOf { it.index * it.score(22, mapOf(Material.ORE to 1), mapOf(), max) }
        return TODO()
    }

    fun part2(input: String): Int {
        return TODO()
    }
}

