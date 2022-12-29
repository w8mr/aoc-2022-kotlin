package aoc2022

import aoc.*

class Day19() {
    //Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
    enum class Material {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE
    }

    data class Cost(val amount: Int, val material: Material)
    data class BuildInstruction(val type: Material, val costs: Map<Material, Cost>)
    data class Blueprint(val index: Int, val buildInstruction: Map<Material, BuildInstruction>)

    val material = byEnum(Material::class)
    val index = "Blueprint " followedBy number() followedBy ":"
    val cost = seq(number(), " " followedBy material, ::Cost)
    val costs = (cost sepBy " and ") map { it.associateBy { Cost::material }}
    val build = seq(" Each " followedBy material, " robot costs " followedBy costs, ::BuildInstruction) followedBy "."
    val builds = zeroOrMore(build) map { it.associateBy(BuildInstruction::type) }
    val line = seq(index, builds, ::Blueprint) followedBy "\n"
    val parser = zeroOrMore(line)

    fun Blueprint.score(timeLeft: Int, robots: Map<Material, Int>, materials: Map<Material, Int>, max: Map<Material, Int>): Int {
        val m = materials.toMutableMap()
        val r = robots.toMutableMap()

        (timeLeft downTo 1).forEach {
            val r2 = r.toMap()

            if (canBuild(materials, Material.GEODE)) {
                println("Bauild geode robot")
                buildRobot(Material.GEODE, m, r)
            } else {
                println("Can't build geode robot")
                if (canBuild(materials, Material.OBSIDIAN)) {
                    println("Can build obsidian robot")
                    val neededObsidian = neededToBuild(m, Material.OBSIDIAN, Material.GEODE)
                    val neededTime = neededObsidian / (r[Material.OBSIDIAN] ?: 0)
                    val neededOre = neededToBuild(m, Material.ORE, Material.GEODE)
                    val hasOreWhenBuildObsidian = m[Material.ORE] ?: 0 + (r[Material.ORE] ?: 0 * neededTime)
                    if (hasOreWhenBuildObsidian > neededOre) {
                        println("Build obsidian robot")
                        buildRobot(Material.OBSIDIAN, m, r)
                    }
                }
            }
            mine(r2, m)
            println("$m, $r")

        }
        println(m)
        println(r)

        return m[Material.GEODE]?:0
    }

    private fun Blueprint.canBuild(materials: Map<Material, Int>, material: Material) =
        buildInstruction[material]?.costs?.values?.all { cost -> materials[cost.material] ?: 0 >= cost.amount } == true

    private fun mine(
        r2: Map<Material, Int>,
        m: MutableMap<Material, Int>
    ) {
        r2.forEach { robot ->
            m.merge(robot.key, robot.value) { old, _ -> old + robot.value }
        }
    }

    private fun Blueprint.buildRobot(
        type: Material,
        m: MutableMap<Material, Int>,
        r: MutableMap<Material, Int>
    ) {
        val inst = buildInstruction[type]!!
        if (inst.costs.all { cost -> m[cost.material] ?: 0 >= cost.amount }) {
            r.merge(type, 1) { old, _ -> old + 1 }
            inst.costs.forEach { cost ->
                m.merge(cost.material, -cost.amount) { old, _ -> old - cost.amount }
            }
        }
    }


    fun part1(input: String): Int {
        val parsed = parser.parse(input)
        val max = mapOf(Material.ORE to 1, Material.CLAY to 4, Material.OBSIDIAN to 2, Material.GEODE to 2)
        println(parsed[0].score(24, mapOf(Material.ORE to 1), mapOf(), max))
        //return parsed.sumOf { it.index * it.score(22, mapOf(Material.ORE to 1), mapOf(), max) }
        return TODO()
    }

    fun part2(input: String): Int {
        return TODO()
    }
}

    private fun Blueprint.neededToBuild(m: MutableMap<Material, Int>, material: Material, robot: Material) =
        buildInstruction[robot]!!.costs[material]!!.amount - (m[material] ?: 0)

