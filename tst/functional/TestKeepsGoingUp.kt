package functional

import agent.*
import exception.HitWallException
import exception.InvalidVectorException
import game.Game
import map.Actor
import map.Coordinate
import map.WorldMapBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class TestKeepsGoingUp {

    @Test
    fun testWinsWithNoWalls() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    ...
                    .s.
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = AlwaysGoUpPlayer(actor)
        val game = Game(player, map)

        game.run()
        assertThat(map.positionFor(actor), equalTo(exitPosition))
        assertThat(actor.alive, equalTo(true))
    }

    @Test
    fun testDoesntWinWithWalls() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    XXX
                    .s.
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = AlwaysGoUpPlayer(actor)
        val game = Game(player, map)
        game.run()

        assertNotEquals(map.positionFor(actor), exitPosition)
        assertTrue(player.feedback is HitWallException)
    }

    @Test
    fun testNavigatesWallAndWins() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    .X.
                    .s.
                """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = GeneralPlayer(actor)
        val game = Game(player, map)
        game.run()

        assertEquals(exitPosition, map.positionFor(actor))
        assertEquals(true, actor.alive)
    }

    @Test
    fun testDoesntWinWithGameRulesBeingViolated() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    ...
                    .s.
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = TeleportingPlayer(actor)
        val game = Game(player, map)
        game.run()

        assertNotEquals(map.positionFor(actor), exitPosition)
        assertTrue(player.feedback is InvalidVectorException)
    }

    @Test
    fun testBFS() {
        val builder = WorldMapBuilder()
                .load("""
                    .........
                    ...X.X...
                    ...XeX...
                    ...XXX...
                    .........
                    .........
                    ....s....
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = BFSPlayerGayatri(actor)
        player.setWorldMap(map)
        val game = Game(player, map)
        game.run()

        assertEquals(exitPosition, map.positionFor(actor))
        assertEquals(true, actor.alive)
    }

    @Test
    fun testExploratoryPlayerWithEndNode() {
        val builder = WorldMapBuilder()
                .load("""
                    .....X...
                    ....eX...
                    ..XXXX...
                    .XX......
                    .X.......
                    .X.......
                    .X......s
                    .........
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val memory = Memory(map.getAllCoordinates())

        val player = ExploratoryPlayerWithEndNode(actor, memory, exitPosition)
        val game = Game(player, map)
        game.run()

        assertEquals(exitPosition, map.positionFor(actor))
        assertEquals(true, actor.alive)
    }

    @Test
    fun testExploratoryPlayerWithNoEndNode() {
        val builder = WorldMapBuilder()
                .load("""
                    .....X...
                    ...sXX...
                    ..XXXX...
                    .XX......
                    .X.......
                    .X.......
                    .X......e
                    .........
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val memory = Memory(map.getAllCoordinates())
        val player = ExploratoryPlayerWithoutEndNode(actor, memory, map.positionFor(actor))
        val game = Game(player, map)
        game.run()

        assertEquals(exitPosition, map.positionFor(actor))
        assertEquals(true, actor.alive)
    }

    @Test
    fun testAlexeysExploringPlayer() {
        val builder = WorldMapBuilder()
                .load("""
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................s...............
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ................................
                    ...............................e
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = AlexeysExploringPlayer(
                actor,
                map.getAllCoordinates(),
                AlexeysExploringPlayer.Spiral(sqrt(2.0)*3-1, map.positionFor(actor)),
                AlexeysExploringPlayer.AStarPather(),
        )
        val game = Game(player, map)
        game.run()

        assertEquals(exitPosition, map.positionFor(actor))
        assertEquals(true, actor.alive)
    }

}

