package me.honnold.sc2protocol

import me.honnold.mpq.Archive
import java.nio.file.Paths

class SC2Protocol(filepath: String) {
    val archive = Archive(Paths.get(filepath))
}