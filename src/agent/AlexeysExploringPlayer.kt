package agent

import action.Action
import action.Move
import exception.InvalidMoveException
import map.*
import map.Vector
import java.lang.Integer.max
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.*

class AlexeysExploringPlayer(
        override val actor: Actor,  // Bad API: actor has methods that should not be exposed to agents
        allCoordinates: Set<Coordinate>,
        private val sequence: Iterator<Coordinate>,
        private val pather: Pather,
) : Player {

    override var feedback: InvalidMoveException? = null

    private var curDest: Coordinate? = null
    private var memory: Memory = Memory(allCoordinates)

    override fun chooseNextMove(): Action {
        val surroundingMap = actor.getSurrounding()
        memory.remember(surroundingMap)

        // So-so API: to get the currentPos, you HAVE to get the entire surrounding and search through that
        val curPos = surroundingMap.positionFor(actor)

        var nextPos: Coordinate?

        val curExitCoord = memory.exitCoord
        if (curExitCoord != null) {
            nextPos = pather.findPath(memory, curPos, curExitCoord)
            if (nextPos == null) {
                throw IllegalStateException("exit node is unreachable")
            }
        } else {
            if (curDest == curPos) {
                curDest = null
            }

            var candidateDest = curDest
            while(true) {
                if (candidateDest == null) {
                    candidateDest = chooseNextDestination(curPos)
                }
                nextPos = pather.findPath(memory, curPos, candidateDest)
                if (nextPos != null) {
                    curDest = candidateDest
                    break;
                }
                candidateDest = chooseNextDestination(curPos)
            }

            if (nextPos == null) throw IllegalStateException("could not choose next destination")
        }

        return Move(Vector(nextPos.x-curPos.x, nextPos.y-curPos.y))
    }

    private fun chooseNextDestination(curPos: Coordinate): Coordinate {
        if (sequence.hasNext()) {
            val next = sequence.next()
            if (memory.isUnobserved(next)) {
                return next
            }

            if (memory.allBorderNodesObserved()) {
                return memory.closestUnobserved(curPos)
            }

            return memory.closestUnobserved(next)
        }

        return memory.closestUnobserved(curPos)
    }

    interface PathableMap {
        // safe to call with 'out-of-map' coordinates
        fun isOpen(coord: Coordinate): Boolean
        fun mapSize(): Coordinate
    }

    interface Pather {
        fun findPath(map: PathableMap, src: Coordinate, dst: Coordinate): Coordinate?
    }

    class AStarPather : Pather {
        override fun findPath(map: PathableMap, src: Coordinate, dst: Coordinate): Coordinate? {
            val explored = MemoryMap<Int?>(map.mapSize()) { null }
            val comparator: Comparator<Coordinate> = ChebyshevComparator(explored, dst)
            val queue = PriorityQueue(comparator)
            explored.set(src, 0)
            queue.add(src)

            // run A*
            while(!queue.isEmpty()) {
                val coord = queue.remove()
                if (coord == dst) break

                val distance = explored.get(coord) ?: throw IllegalStateException("dequeued coord had no distance cost")

                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if (dx!=0 || dy!=0) {
                            val candidate = Coordinate(coord.x+dx, coord.y+dy)
                            if (map.isOpen(candidate) && explored.get(candidate) == null) {
                                explored.set(candidate, distance+1)
                                queue.add(candidate)
                            }
                        }
                    }
                }
            }

            // if no path found - return
            if (explored.get(dst) == null) return null

            // otherwise let's traceback
            var back = dst
            outer@ while(true) {
                val backDist = explored.get(back) ?: throw IllegalStateException("traceback coord has no distance cost")
                if (backDist == 1) return back


                // prefer straight moves over diagonal
                // straight
                for (i in 0..3 step 2) {
                    val di = i%2*2 - 1
                    val next = if (i/2 == 0) Coordinate(back.x, back.y+di) else Coordinate(back.x+di, back.y)
                    if (map.isOpen(next) && explored.get(next) == backDist-1) {
                        back = next
                        continue@outer
                    }
                }
                //diagonal
                for (i in 0..3) {
                    val dx = i%2*2 - 1
                    val dy = i/2*2 - 1
                    val next = Coordinate(back.x+dx, back.y+dy)
                    if (map.isOpen(next) && explored.get(next) == backDist-1) {
                        back = next
                        continue@outer
                    }
                }

                throw java.lang.IllegalStateException("traceback could not find next step")
            }
        }

        class ChebyshevComparator(private val explored: MemoryMap<Int?>, val dst: Coordinate) : Comparator<Coordinate> {
            override fun compare(l: Coordinate, r: Coordinate): Int {
                return f(l).compareTo(f(r))
            }

            private fun f(l: Coordinate): Int {
                val g = explored.get(l) ?: throw IllegalStateException("no distance cost in comparator")
                return max(abs(dst.x - l.x), abs(dst.y - l.y)) + g
            }
        }
    }

    class Spiral(
            private val d: Double,
            private val start: Coordinate,
    ) : Iterator<Coordinate> {

        private val r = d / 2 / Math.PI

        private var i = 1

        override fun hasNext(): Boolean {
            return true
        }

        override fun next(): Coordinate {
            val t = sqrt(1/r*2*d*i++)
            val rt = r * t
            val x = rt * cos(t) + start.x
            val y = rt * sin(t) + start.y
            return Coordinate(x.toInt(), y.toInt()) // floor, not random, to keep it tighter to the start and prevent gaps
        }
    }

    class Memory(allCoordinates: Set<Coordinate>) : PathableMap {
        private val mapSize: Coordinate = calculateMapSize(allCoordinates)

        // null - present on the map, unobserved (possibly open)
        // false - definitely closed (wall or not present on the map)
        // true - observed open
        private val stored: MemoryMap<Boolean?> = calculateStartingStored(allCoordinates, mapSize)

        private val unobservedBorderNodes: MutableSet<Coordinate> = calculateBorderNodes(allCoordinates)

        // not null if observed the exit node
        var exitCoord: Coordinate? = null

        private fun calculateStartingStored(allCoordinates: Set<Coordinate>, mapSize: Coordinate): MemoryMap<Boolean?> {
            val arr = MemoryMap<Boolean?>(mapSize) { null }
            for(x in 0 until mapSize.x) {
                for(y in 0 until mapSize.y) {
                    if (!allCoordinates.contains(Coordinate(x, y))) {
                        arr.set(Coordinate(x, y), false)
                    }
                }
            }
            return arr
        }

        private fun calculateMapSize(coordinates: Set<Coordinate>): Coordinate {
            var maxx = 0
            var maxy = 0
            for (coord in coordinates) {
                if (coord.x > maxx) maxx = coord.x
                if (coord.y > maxy) maxy = coord.y
            }
            return Coordinate(maxx+1, maxy+1)
        }

        private fun calculateBorderNodes(allCoordinates: Set<Coordinate>): MutableSet<Coordinate> {
            val result = HashSet<Coordinate>()
            for (coord in allCoordinates) {
                for (adj in neighboursOf(coord)) {
                    if (!allCoordinates.contains(adj)) {
                        result.add(coord)
                        break
                    }
                }
            }
            return result
        }

        override fun mapSize(): Coordinate {
            return mapSize
        }

        // true if open
        // false if closed or not present
        // null if unobserved
        fun get(coord: Coordinate): Boolean? {
            return stored.get(coord)
        }

        // true if open
        // false if closed
        fun set(coord: Coordinate, v: Boolean) {
            stored.set(coord, v)
            unobservedBorderNodes.remove(coord)
        }

        override fun isOpen(coord: Coordinate): Boolean {
            if (!isOnMap(coord)) {
                return false
            }
            val v = get(coord)
            return v == null || v
        }

        // not 100% precise, only points outside of bound rect are 'not on map'
        fun isOnMap(coord: Coordinate): Boolean {
            if (coord.x < 0 || coord.x >= mapSize.x) {
                return false
            }
            if (coord.y < 0 || coord.y >= mapSize.y) {
                return false
            }
            return true
        }

        fun remember(surroundingMap: WorldMap) {
            for(coord in surroundingMap.getAllCoordinates()) {
                when (surroundingMap.getNode(coord)) {
                    is ExitNode -> {
                        set(coord, true)
                        exitCoord = coord
                    }
                    is OpenSpaceNode ->
                        set(coord, true)
                    is WallNode ->
                        set(coord, false)
                }
            }
        }

        fun isUnobserved(coord: Coordinate): Boolean {
            if (!isOnMap(coord)) {
                return false
            }
            return get(coord) == null
        }

        fun allBorderNodesObserved(): Boolean {
            return unobservedBorderNodes.isEmpty()
        }

        fun closestUnobserved(coord: Coordinate): Coordinate {
            var minDist2: Long? = null
            var closestUnobs: Coordinate? = null

            // TODO can be optimized
            for (x in 0 until mapSize.x) {
                for (y in 0 until mapSize.y) {
                    val candidate = Coordinate(x, y)
                    if (isUnobserved(candidate)) {
                        val dist2 = dist2(candidate, coord)
                        if (minDist2 == null || minDist2 > dist2) {
                            minDist2 = dist2
                            closestUnobs = candidate
                        }
                    }
                }
            }

            if (closestUnobs == null) {
                throw IllegalStateException("map is fully explored")
            }
            return closestUnobs
        }

    }

    class MemoryMap<T>(val mapSize: Coordinate, init: (Coordinate) -> T) {

        private val arr: MutableList<T> = ArrayList(mapSize.x*mapSize.y)

        init {
            for (y in 0 until mapSize.y) {
                for (x in 0 until mapSize.x) {
                    val coord = Coordinate(x, y)
                    arr.add(init(coord))
                }
            }
        }

        fun get(coord: Coordinate): T {
            return arr[coord.y*mapSize.x + coord.x]
        }

        fun set(coord: Coordinate, v: T) {
            arr[coord.y*mapSize.x + coord.x] = v
        }

    }
}

private fun dist2(src: Coordinate, dst: Coordinate): Long {
    var dx2: Long = (src.x-dst.x).toLong()
    dx2 *= dx2
    var dy2: Long = (src.y-dst.y).toLong()
    dy2 *= dy2
    return dx2+dy2
}

private fun neighboursOf(coord: Coordinate): Set<Coordinate> {
    val result = HashSet<Coordinate>()
    for (dx in -1..1) {
        for (dy in -1..1) {
            if (dx != 0 || dy != 0) {
                result.add(Coordinate(coord.x+dx, coord.y+dy))
            }
        }
    }
    return result
}
