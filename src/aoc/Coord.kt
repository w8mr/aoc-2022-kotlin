package aoc

data class Coord(val x: Int = 0, val y: Int = 0) {
    fun up()    = Coord(x, y + 1)
    fun down()  = Coord(x, y - 1)
    fun right() = Coord(x + 1, y)
    fun left()  = Coord(x - 1, y)
}
