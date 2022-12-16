package aoc2022

import aoc.*
import kotlin.math.absoluteValue

class Day15() {
    data class SensorBeacon(val sensorLocation: Coord, val beaconLocation: Coord)

    // Sensor at x=2, y=18: closest beacon is at x=-2, y=15
    val coord = seq("x=" followedBy number(), ", y=" followedBy number(),::Coord)
    val line = seq("Sensor at " followedBy coord, ": closest beacon is at " followedBy coord, ::SensorBeacon) followedBy "\n"
    val lines = zeroOrMore(line)


    fun part1(input: String, test: Int): Int {

        val s3 = findNonBeaconOnRow(input, test)

        return s3.sumOf { it.last - it.first }
    }

    private fun findNonBeaconOnRow(input: String, test: Int): List<IntRange> {
        val parsed = lines.parse(input)
        val r = parsed.map { sensorBeacon ->
            val carthesianDistanceSensorBeacon =
                (sensorBeacon.sensorLocation.x - sensorBeacon.beaconLocation.x).absoluteValue +
                        (sensorBeacon.sensorLocation.y - sensorBeacon.beaconLocation.y).absoluteValue
            val rowDistanceSensorTest = (sensorBeacon.sensorLocation.y - test).absoluteValue
            val overlap = carthesianDistanceSensorBeacon - rowDistanceSensorTest
            when {
                overlap >= 0 -> sensorBeacon.sensorLocation.x - overlap..sensorBeacon.sensorLocation.x + overlap
                else -> null
            }
        }.filterNotNull()
        return union(r)
    }


    fun part2(input: String, size:Int): Long {
        val y = (0..size).find { row -> findNonBeaconOnRow(input, row).size > 1 }
        val beacons = findNonBeaconOnRow(input, y!!)
        val x = beacons[0].last+1
        return 4000000L * x + y
    }
}

