package me.honnold.sc2protocol

import org.junit.Test
import kotlin.test.assertNotNull

class SC2ProtocolTest {
    @Test
    fun loadsArchive() {
        val sc2protocol = SC2Protocol("src/test/resources/archive.sc2replay")

        assertNotNull(sc2protocol)
    }
}