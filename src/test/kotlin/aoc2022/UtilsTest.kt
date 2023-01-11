package aoc2022

import aoc.*
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class UtilsTest {
    @Test
    fun gcd2() {
        assertTrue { 50.gcd(20) == 10 }
    }

    @Test
    fun gcd3() {
        assertTrue { 50.gcd(20, 8) == 2 }
    }

    @Test
    fun lcm2() {
        assertTrue { 6.lcm(15) == 30 }
    }

    @Test
    fun lcm3() {
        assertTrue { 6.lcm(15, 8) == 120 }
    }

}

