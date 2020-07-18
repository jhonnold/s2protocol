package me.honnold.sc2protocol

import me.honnold.mpq.Archive
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
        val decodedHeader = p.decodeHeader(contents) as Map<String, Any?>

        assertTrue(decodedHeader["m_useScaledTime"] as Boolean)
        assertEquals(80188L, decodedHeader["m_dataBuildNum"])
        assertEquals(10318L, decodedHeader["m_elapsedGameLoops"])
        assertEquals(2L, decodedHeader["m_type"])

        val version = decodedHeader["m_version"] as Map<String, Any?>
        assertEquals(0L, version["m_revision"])
        assertEquals(80188L, version["m_build"])
        assertEquals(1L, version["m_flags"])
        assertEquals(4L, version["m_major"])
        assertEquals(12L, version["m_minor"])
        assertEquals(80188L, version["m_baseBuild"])

        val signature = toString(decodedHeader["m_signature"] as ByteBuffer)
        assertTrue(signature.startsWith("StarCraft II replay"))
    }

    @Test
    fun decodeDetails() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.getFileContents("replay.details")

        val p = Protocol(80188)
        val decodedDetails = p.decodeDetails(contents) as Map<String, Any?>

        assertEquals("Ever Dream LE", toString(decodedDetails["m_title"] as ByteBuffer))

        val players = decodedDetails["m_playerList"] as List<Map<String, Any?>>

        assertEquals("Terran", toString(players[1]["m_race"] as ByteBuffer))
        assertEquals("&lt;xACABx&gt;<sp/>Zomby", toString(players[1]["m_name"] as ByteBuffer))
    }

    @Test
    fun decodeInitData() {
        val resource = this::class.java.getResource("/archive.sc2replay")
        val archive = Archive(Paths.get(resource.toURI()))
        val contents = archive.getFileContents("replay.initData")

        val p = Protocol(80188)
        val decodedInitData = p.decodeInitData(contents) as Map<String, Any?>

        val userInitData =
            (decodedInitData["m_syncLobbyState"] as Map<String, Any?>)["m_userInitialData"] as List<Map<String, Any?>>

        assertEquals(16, userInitData.size)
        assertEquals(5L, userInitData[1]["m_highestLeague"])
        assertEquals("xACABx", toString(userInitData[1]["m_clanTag"] as ByteBuffer))
        assertEquals("Zomby", toString(userInitData[1]["m_name"] as ByteBuffer).toString())
        assertEquals(3908L, userInitData[1]["m_scaledRating"])

        val gameDescription =
            (decodedInitData["m_syncLobbyState"] as Map<String, Any?>)["m_gameDescription"] as Map<String, Any?>

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
}