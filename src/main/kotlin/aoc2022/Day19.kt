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

    data class BlueprintInt(val index: Int, val oreRobotOre: Int, val clayRobotOre: Int, val obsidianRobotOre: Int,
                            val obsidianRobotClay: Int, val geodeRobotOre: Int, val geodeRobotObsidian: Int)

    fun blueprint(index: Int, buildInstruction: Map<Material, BuildInstruction>): BlueprintInt {
        fun amount(buildInstruction: Map<Material, BuildInstruction>, robot: Material, material: Material) =
            buildInstruction[robot]?.costs?.get(material)?.amount ?: 0
        return BlueprintInt(index,
            amount(buildInstruction, Material.ORE, Material.ORE),
            amount(buildInstruction, Material.CLAY, Material.ORE),
            amount(buildInstruction, Material.OBSIDIAN, Material.ORE),
            amount(buildInstruction, Material.OBSIDIAN, Material.CLAY),
            amount(buildInstruction, Material.GEODE, Material.ORE),
            amount(buildInstruction, Material.GEODE, Material.OBSIDIAN)
        )
    }

    val material = byEnum(Material::class)
    val index = "Blueprint " followedBy number() followedBy ":"
    val cost = seq(number(), " " followedBy material, ::Cost)
    val costs = cost sepBy " and " map { it.associateBy(Cost::material) }
    val build = seq(" Each " followedBy material, " robot costs " followedBy costs, ::BuildInstruction) followedBy "."
    val builds = zeroOrMore(build) map { it.associateBy(BuildInstruction::type) }
    val line = seq(index, builds, ::blueprint) followedBy "\n"
    val parser = zeroOrMore(line)

    fun BlueprintInt.score(timeLeft: Int): Int {
        val robotCosts = listOf(
            listOf(oreRobotOre, 0, 0, 0),
            listOf(clayRobotOre, 0, 0, 0),
            listOf(obsidianRobotOre, obsidianRobotClay, 0, 0),
            listOf(geodeRobotOre, 0, geodeRobotObsidian, 0))

        val maxOreNeeded = maxOf(oreRobotOre, clayRobotOre, obsidianRobotOre, geodeRobotOre)

        val maxNeeded = listOf(maxOreNeeded, obsidianRobotClay, geodeRobotObsidian, timeLeft)

        fun go(
            timeLeft: Int,
            robots: List<Int>,
            amounts: List<Int>
        ): Int {
            if (timeLeft <= 0) {
                return amounts[3]
            }

            fun goGeneric(robotIdx: Int) =
                if ((robots[robotIdx] >= maxNeeded[robotIdx])) 0 else {
                    val canBuild = (0..3).all { costIdx -> robotCosts[robotIdx][costIdx] == 0 || robots[costIdx] > 0 }
                    if (!canBuild) 0 else {
                        val timeNeeded = (0..3).maxOf { costIdx ->
                            if (robotCosts[robotIdx][costIdx] <= 0) 0
                            else maxOf(
                                0,
                                robotCosts[robotIdx][costIdx] - amounts[costIdx] + robots[costIdx] - 1
                            ) / robots[costIdx]
                        } + 1
                        if (timeLeft - timeNeeded < 0) {
                            if (robots[3] == 0) 0
                            else amounts[3] + timeLeft * robots[3]
                        } else {
                            val newRobots = robots.mapIndexed { index, count -> if (robotIdx == index) count + 1 else count}
                            val newAmounts = amounts.mapIndexed { index, amount -> amount + timeNeeded * robots[index] - robotCosts[robotIdx][index]}
                            go(
                                timeLeft - timeNeeded,
                                newRobots,
                                newAmounts
                            )
                        }
                    }
                }

            return (0..3).maxOf(::goGeneric)
        }

        val result = go(timeLeft, listOf(1, 0, 0, 0), listOf(0, 0, 0, 0))
        println("Blueprint $index: ${result}")
        return result
    }

    fun part1(input: String): Int {
        val parsed = parser.parse(input)
        return parsed.sumOf { it.index * it.score(24) }
    }

    fun part2(input: String): Int {
        val parsed = parser.parse(input)
        return parsed.take(3).map { it.score(32) }.product()
    }
}

/*
    fun BlueprintInt.score(timeLeft: Int): Int {
        val maxOreNeeded = maxOf(oreRobotOre, clayRobotOre, obsidianRobotOre, geodeRobotOre)

        fun go(timeLeft: Int,
               oreRobots: Int, clayRobots: Int, obsidianRobots: Int, geodeRobots: Int,
               oreAmount: Int, clayAmount: Int, obsidianAmount: Int, geodeAmount: Int
        ): Int {
            if (timeLeft <= 0) {
                return geodeAmount
            }

            fun goOre() =
                if (oreRobots >= maxOreNeeded) 0 else {
                    val timeNeeded = (maxOf(0, oreRobotOre - oreAmount + oreRobots - 1) / oreRobots) + 1
                    if (timeLeft - timeNeeded <0) geodeAmount
                    else go(
                        timeLeft - timeNeeded,
                        oreRobots + 1,
                        clayRobots,
                        obsidianRobots,
                        geodeRobots,
                        oreAmount + timeNeeded * oreRobots - oreRobotOre,
                        clayAmount + timeNeeded * clayRobots,
                        obsidianAmount + timeNeeded * obsidianRobots,
                        geodeAmount + timeNeeded * geodeRobots
                    )
                }


            fun goClay() =
                if (clayRobots >= obsidianRobotClay) 0 else {
                    val timeNeeded = (maxOf(0, clayRobotOre - oreAmount + oreRobots - 1) / oreRobots) + 1
                    if (timeLeft - timeNeeded <0) geodeAmount
                    else go(
                        timeLeft - timeNeeded,
                        oreRobots,
                        clayRobots + 1,
                        obsidianRobots,
                        geodeRobots,
                        oreAmount + timeNeeded * oreRobots - clayRobotOre,
                        clayAmount + timeNeeded * clayRobots,
                        obsidianAmount + timeNeeded * obsidianRobots,
                        geodeAmount + timeNeeded * geodeRobots
                    )
                }


            fun goObsidian() =
                if ((obsidianRobots >= geodeRobotObsidian) || (clayRobots ==0)) 0 else {
                    val timeNeededOre = (maxOf(0, obsidianRobotOre - oreAmount + oreRobots - 1) / oreRobots)
                    val timeNeededClay = (maxOf( 0, obsidianRobotClay - clayAmount + clayRobots - 1) / clayRobots)
                    val timeNeeded = maxOf(timeNeededOre, timeNeededClay) + 1
                    if (timeLeft - timeNeeded <0) geodeAmount
                    else go(
                        timeLeft - timeNeeded,
                        oreRobots,
                        clayRobots,
                        obsidianRobots + 1,
                        geodeRobots,
                        oreAmount + timeNeeded * oreRobots - obsidianRobotOre,
                        clayAmount + timeNeeded * clayRobots - obsidianRobotClay,
                        obsidianAmount + timeNeeded * obsidianRobots,
                        geodeAmount + timeNeeded * geodeRobots
                    )
                }

            fun goGeode() =
                if (obsidianRobots == 0) 0 else {
                    val timeNeededOre = (maxOf(0, geodeRobotOre - oreAmount + oreRobots - 1) / oreRobots)
                    val timeNeededObsidian = (maxOf(0, geodeRobotObsidian - obsidianAmount + obsidianRobots - 1) / obsidianRobots)
                    val timeNeeded = maxOf(timeNeededOre, timeNeededObsidian) + 1
                    if (timeLeft - timeNeeded <0) geodeAmount + geodeRobots * timeLeft
                    else go(
                        timeLeft - timeNeeded,
                        oreRobots,
                        clayRobots,
                        obsidianRobots,
                        geodeRobots + 1,
                        oreAmount + timeNeeded * oreRobots - geodeRobotOre,
                        clayAmount + timeNeeded * clayRobots,
                        obsidianAmount + timeNeeded * obsidianRobots - geodeRobotObsidian,
                        geodeAmount + timeNeeded * geodeRobots
                    )
                }

            val tries = listOf(
                goOre(),
                goClay(),
                goObsidian(),
                goGeode()
            )
            val best = tries.max()
            return best
        }

        val result = go(timeLeft, 1, 0, 0, 0, 0, 0, 0, 0)
        println("Blueprint $index: ${result}")
        return result
    }

 */


/*

    fun BlueprintInt.scoreBreathFirst(timeLeft: Int): Int {
        data class State(val time: Int = 0,
                         val robots: List<Int> = listOf(1, 0, 0, 0),
                         val amounts: List<Int> = listOf(0, 0, 0, 0),
                         val score: Int = 0,
                         val oldState : State? = null)

        val queue: Queue<State> = LinkedList()
        val seen = mutableSetOf<State>()

        val maxNeeded = listOf(maxOf(oreRobotOre, clayRobotOre, obsidianRobotOre, geodeRobotOre), obsidianRobotClay, geodeRobotObsidian, timeLeft)

        val initialState = State()
        queue.add(initialState)

        var maxState = initialState

        val robotCosts = listOf(
            listOf(oreRobotOre, 0, 0, 0),
            listOf(clayRobotOre, 0, 0, 0),
            listOf(obsidianRobotOre, obsidianRobotClay, 0, 0),
            listOf(geodeRobotOre, 0, geodeRobotObsidian, 0))

        while (queue.isNotEmpty()) {
            val state = queue.remove()
            seen.add(state)
            val (time, robots, amounts, score) = state

            (0..3).forEach { robotIdx ->
                if ((robots[robotIdx]<maxNeeded[robotIdx])) {
                    val canBuild = (0..3).all { costIdx ->
                        robotCosts[robotIdx][costIdx] == 0 || (robots[costIdx] > 0)
                    }
                    if (canBuild) {


                        val timeTillBuild = (0..3).maxOf { costIdx ->
                            if (robots[costIdx] <= 0) 0
                            else maxOf(0, robotCosts[robotIdx][costIdx] - amounts[costIdx] + robots[costIdx] - 1) / robots[costIdx]
                        } + 1


                        val newTime = time + timeTillBuild
                        if (newTime <= timeLeft) {
                            val newRobots = robots.mapIndexed { index, i -> if (index == robotIdx) i + 1 else i }
                            val newAmounts =
                                amounts.mapIndexed { index, i -> i - robotCosts[robotIdx][index] + timeTillBuild * robots[index] }
                            val newScore = newAmounts[3] + newRobots[3] * (timeLeft - newTime)
                            val newState = State(newTime, newRobots, newAmounts, newScore, state)
                            if (newState !in seen) queue.add(newState)
                        }
                    }
                }

            }
            if (score > maxState.score) {
                maxState = state
            }
        }
        println("Blueprint $index: ${maxState.score} ($maxState)")

        return maxState.score
    }



 */