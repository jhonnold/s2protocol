package me.honnold.sc2protocol

import me.honnold.sc2protocol.decoder.BitDecoder
import me.honnold.sc2protocol.decoder.VersionedBitDecoder
import me.honnold.sc2protocol.model.type.TypeInfo
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class Protocol(build: Int) {
    companion object {
        private val EVENT_REGEX = Regex("([\\d]+):\\s+\\(([\\d]+),\\s+'([a-zA-Z.]+)'\\)")
        private val TYPE_ID_REGEX = Regex("([\\w]+)\\s+=\\s+([\\d]+)")
    }

    private val infos = ArrayList<TypeInfo>()

    var gameEventsTypeId = -1
    val gameEvents = HashMap<Int, Pair<Int, String>>()

    var messageEventTypeId = -1
    val messageEvents = HashMap<Int, Pair<Int, String>>()

    var trackerEventTypeId = -1
    val trackerEvents = HashMap<Int, Pair<Int, String>>()

    var gameLoopDeltaTypeId = -1
    var replayUserIdTypeId = -1
    var replayHeaderTypeId = -1
    var gameDetailsTypeId = -1
    var replayInitTypeId = -1

    init {
        val resource = this::class.java.getResource("/data/$build.dat")
            ?: throw MissingResourceException("No data file for $build", null, null)

        val reader = BufferedReader(InputStreamReader(resource.openStream()))

        reader.useLines { lines ->
            val iterator = lines.iterator()

            var line: String
            while (iterator.hasNext()) {
                line = iterator.next()
                if (line.isEmpty()) break

                this.infos.add(TypeInfo.from(line))
            }

            while (iterator.hasNext()) {
                line = iterator.next()
                if (line.isEmpty()) break

                val entry = this.readEventLine(line)
                gameEvents[entry.key] = entry.value
            }

            line = iterator.next()
            this.gameEventsTypeId = this.readTypeId(line)

            while (iterator.hasNext()) {
                line = iterator.next()
                if (line.isEmpty()) break

                val entry = this.readEventLine(line)
                messageEvents[entry.key] = entry.value
            }

            line = iterator.next()
            this.messageEventTypeId = this.readTypeId(line)

            while (iterator.hasNext()) {
                line = iterator.next()
                if (line.isEmpty()) break

                val entry = this.readEventLine(line)
                trackerEvents[entry.key] = entry.value
            }

            line = iterator.next()
            this.trackerEventTypeId = this.readTypeId(line)

            this.gameLoopDeltaTypeId = this.readTypeId(iterator.next())
            this.replayUserIdTypeId = this.readTypeId(iterator.next())
            this.replayHeaderTypeId = this.readTypeId(iterator.next())
            this.gameDetailsTypeId = this.readTypeId(iterator.next())
            this.replayInitTypeId = this.readTypeId(iterator.next())
        }
    }

    fun decodeHeader(contents: ByteBuffer): Any? {
        val decoder = VersionedBitDecoder(this.infos, contents)

        return decoder.get(this.replayHeaderTypeId)
    }

    fun decodeDetails(contents: ByteBuffer): Any? {
        val decoder = VersionedBitDecoder(this.infos, contents)

        return decoder.get(this.gameDetailsTypeId)
    }

    fun decodeInitData(contents: ByteBuffer): Any? {
        val decoder = BitDecoder(this.infos, contents)

        return decoder.get(this.replayInitTypeId)
    }

    private fun readEventLine(line: String): AbstractMap.SimpleEntry<Int, Pair<Int, String>> {
        val eventMatch = EVENT_REGEX.find(line) ?: throw IllegalArgumentException("Unable to parse event from $line")

        val key = eventMatch.groups[1]!!.value.toInt()
        val typeId = eventMatch.groups[2]!!.value.toInt()
        val name = eventMatch.groups[3]!!.value

        return AbstractMap.SimpleEntry(key, Pair(typeId, name))
    }

    private fun readTypeId(line: String): Int {
        val gameEventTypeIdMatch =
            TYPE_ID_REGEX.find(line) ?: throw IllegalArgumentException("Unable to parse type id from $line")

        return gameEventTypeIdMatch.groups[2]!!.value.toInt()
    }
}
