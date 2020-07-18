package me.honnold.sc2protocol.model.event

import me.honnold.sc2protocol.model.data.Struct

class Event(val id: Long, val name: String, val loop: Long, val user: Struct?, val data: Struct) {
    override fun toString(): String {
        return "Event(id=$id, name=$name, loop=$loop, user=$user, data=$data)"
    }
}