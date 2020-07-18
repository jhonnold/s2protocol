package me.honnold.sc2protocol

import me.honnold.mpq.Archive
import me.honnold.sc2protocol.model.Struct
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

fun toString(buffer: ByteBuffer) = StandardCharsets.UTF_8.decode(buffer).toString()

class ProtocolTest {
    @Test
    fun decodeHeader() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.userData!!.content

        val p = Protocol(80188)
        val decodedHeader = p.decodeHeader(contents)

        assertTrue(decodedHeader["m_useScaledTime"])
        assertEquals(80188L, decodedHeader["m_dataBuildNum"])
        assertEquals(10318L, decodedHeader["m_elapsedGameLoops"])
        assertEquals(2L, decodedHeader["m_type"])

        val version: Struct = decodedHeader["m_version"]
        assertEquals(0L, version["m_revision"])
        assertEquals(80188L, version["m_build"])
        assertEquals(1L, version["m_flags"])
        assertEquals(4L, version["m_major"])
        assertEquals(12L, version["m_minor"])
        assertEquals(80188L, version["m_baseBuild"])

        val signature = toString(decodedHeader["m_signature"])
        assertTrue(signature.startsWith("StarCraft II replay"))
    }

    @Test
    fun decodeDetails() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.getFileContents("replay.details")

        val p = Protocol(80188)
        val decodedDetails = p.decodeDetails(contents)

        assertEquals("Ever Dream LE", toString(decodedDetails["m_title"]))

        val players: List<Struct> = decodedDetails["m_playerList"]

        assertEquals("Terran", toString(players[1]["m_race"]))
        assertEquals("&lt;xACABx&gt;<sp/>Zomby", toString(players[1]["m_name"]))
    }

    @Test
    fun decodeInitData() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.getFileContents("replay.initData")

        val p = Protocol(80188)
        val decodedInitData = p.decodeInitData(contents)
        val syncLobbyState: Struct = decodedInitData["m_syncLobbyState"]

        val userInitData: List<Struct> = syncLobbyState["m_userInitialData"]
        assertEquals(16, userInitData.size)
        assertEquals(5L, userInitData[1]["m_highestLeague"])
        assertEquals("xACABx", toString(userInitData[1]["m_clanTag"] ))
        assertEquals("Zomby", toString(userInitData[1]["m_name"]))
        assertEquals(3908L, userInitData[1]["m_scaledRating"])

        val gameDescription: Struct = syncLobbyState["m_gameDescription"]
        assertEquals(216L, gameDescription["m_mapSizeY"])
        assertEquals(200L, gameDescription["m_mapSizeX"])
    }

    @Test
    fun decodeAttributes() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.getFileContents("replay.attributes.events")

        val p = Protocol(80188)
        val events = p.decodeAttributeEvents(contents)

        assertNotNull(events)
        assertEquals(0, events.source)
        assertEquals(0, events.mapNamespace)

        assertEquals(3, events.scopes.keys.size)
        assertTrue(events.scopes.keys.contains(1))
        assertTrue(events.scopes.keys.contains(2))
        assertTrue(events.scopes.keys.contains(16))

        assertEquals(75, events.scopes[1]!!.keys.size)
        assertEquals(75, events.scopes[2]!!.keys.size)
        assertEquals(10, events.scopes[16]!!.keys.size)

        val attributeFromScope1 = events.scopes[1]!![500]
        assertNotNull(attributeFromScope1)
        assertEquals(999, attributeFromScope1.namespace)
        assertEquals(500, attributeFromScope1.id)
        assertEquals("Humn", attributeFromScope1.value)
        assertEquals(1, attributeFromScope1.scope)

        val attributeFromScope2 = events.scopes[2]!![3172]
        assertNotNull(attributeFromScope2)
        assertEquals(999, attributeFromScope2.namespace)
        assertEquals(3172, attributeFromScope2.id)
        assertEquals("AB00", attributeFromScope2.value)
        assertEquals(2, attributeFromScope2.scope)

        val attributeFromScope16 = events.scopes[16]!![1001]
        assertNotNull(attributeFromScope16)
        assertEquals(999, attributeFromScope16.namespace)
        assertEquals(1001, attributeFromScope16.id)
        assertEquals("yes", attributeFromScope16.value)
        assertEquals(16, attributeFromScope16.scope)
    }

    @Test
    fun decodeGameEvents() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.getFileContents("replay.game.events")

        val p = Protocol(80188)

        val events = p.decodeGameEvents(contents)
        assertEquals(6846, events.size)

        assertEquals(2, events.count { it["eventName"] == "NNet.Game.SGameUserLeaveEvent" })
        assertEquals(
            540,
            events.count { it["eventName"] == "NNet.Game.SCmdUpdateTargetPointEvent" })
        assertEquals(
            2106,
            events.count { it["eventName"] == "NNet.Game.SControlGroupUpdateEvent" })
        assertEquals(
            4,
            events.count { it["eventName"] == "NNet.Game.STriggerSoundLengthSyncEvent" })
        assertEquals(1, events.count { it["eventName"] == "NNet.Game.SSetSyncPlayingTimeEvent" })
        assertEquals(1, events.count { it["eventName"] == "NNet.Game.SSetSyncLoadingTimeEvent" })
        assertEquals(
            1,
            events.count { it["eventName"] == "NNet.Game.SUserFinishedLoadingSyncEvent" })
        assertEquals(15, events.count { it["eventName"] == "NNet.Game.SCameraSaveEvent" })
        assertEquals(765, events.count { it["eventName"] == "NNet.Game.SSelectionDeltaEvent" })
        assertEquals(1996, events.count { it["eventName"] == "NNet.Game.SCameraUpdateEvent" })
        assertEquals(471, events.count { it["eventName"] == "NNet.Game.SCmdEvent" })
        assertEquals(2, events.count { it["eventName"] == "NNet.Game.SUserOptionsEvent" })
        assertEquals(
            855,
            events.count { it["eventName"] == "NNet.Game.SCommandManagerStateEvent" })
        assertEquals(
            87,
            events.count { it["eventName"] == "NNet.Game.SCmdUpdateTargetUnitEvent" })
    }

    @Test
    fun decodeMessageEvents() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.getFileContents("replay.message.events")

        val p = Protocol(80188)

        val events = p.decodeMessageEvents(contents)

        assertEquals(2, events.count { it["eventName"] == "NNet.Game.SChatMessage" })
        assertEquals(17, events.count { it["eventName"] == "NNet.Game.SLoadingProgressMessage" })
    }

    @Test
    fun decodeTrackerEvents() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.getFileContents("replay.tracker.events")

        val p = Protocol(80188)

        val events = p.decodeTrackerEvents(contents)

        assertEquals(
            133,
            events.count { it["eventName"] == "NNet.Replay.Tracker.SPlayerStatsEvent" })
        assertEquals(
            72,
            events.count { it["eventName"] == "NNet.Replay.Tracker.SUnitDoneEvent" })
        assertEquals(
            170,
            events.count { it["eventName"] == "NNet.Replay.Tracker.SUnitDiedEvent" })
        assertEquals(31, events.count { it["eventName"] == "NNet.Replay.Tracker.SUpgradeEvent" })
        assertEquals(
            75,
            events.count { it["eventName"] == "NNet.Replay.Tracker.SUnitInitEvent" })
        assertEquals(
            13,
            events.count { it["eventName"] == "NNet.Replay.Tracker.SUnitPositionsEvent" })
        assertEquals(
            565,
            events.count { it["eventName"] == "NNet.Replay.Tracker.SUnitBornEvent" })
        assertEquals(
            302,
            events.count { it["eventName"] == "NNet.Replay.Tracker.SUnitTypeChangeEvent" })
        assertEquals(
            2,
            events.count { it["eventName"] == "NNet.Replay.Tracker.SPlayerSetupEvent" })
    }
}