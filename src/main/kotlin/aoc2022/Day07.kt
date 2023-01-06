package aoc2022

import aoc.*


class Day07() {
    sealed interface Node {
        val size: Int
        val subNodes: List<Node>
    }

    data class FileNode(override val size: Int, val name: String): Node {
        override val subNodes: List<Node>
            get() = emptyList()
    }

    data class DirNode(val name: String, override val subNodes: List<Node> = emptyList()): Node {
        override val size: Int
            get() = subNodes.sumOf { it.size }
    }

    val fileEntry = seq(number(), Literal(" "), regex("[a-zA-Z./]+"), Literal("\n")) { size, _, n, _ -> FileNode(size, n) }
    val dirEntry = seq(Literal("dir "), regex("[a-zA-Z./]+"), Literal("\n")) { _, n, _ -> DirNode(n) }
    val fileDirEntry = seq(zeroOrMore(dirEntry), fileEntry, zeroOrMore(dirEntry)) { _, e, _ -> e}
    val entry = fileDirEntry or ref(::dirListing)
    val entries = zeroOrMore(entry)
    val dirListing: Parser<Node> = seq(Literal("\$ cd "),
        regex("[a-zA-Z/]+"),
        Literal("\n\$ ls\n"),
        entries,
        Or(Literal("\$ cd ..\n"), EoF())) { _, n, _, e, _ -> DirNode(n, e) }


    fun part1(input: String): Int {
        val tree = dirListing.parse(input)
        val r = foldTree(tree, Node::subNodes, 0) { acc, node ->
            acc + if (node is DirNode && node.size < 100000) node.size else 0
        }
        return r
    }

    fun part2(input: String): Int {
        val minimum = 30000000 - 21618835
        val tree = dirListing.parse(input)
        val r = foldTree(tree, Node::subNodes, null) { acc: Node?, node: Node ->
            if (node is DirNode && node.size >= minimum && node.size < (acc?.size ?: Int.MAX_VALUE)) node else acc
        }
        return r?.size ?: 0
    }
}


