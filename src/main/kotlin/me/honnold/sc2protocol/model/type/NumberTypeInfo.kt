package me.honnold.sc2protocol.model.type

import me.honnold.sc2protocol.model.Bounds

class NumberTypeInfo(bounds: Bounds) : TypeInfo(TypeMethod.NUMBER) {
    override val p: Bounds = bounds

    override fun toString(): String {
        return "('_int',[${this.p}])"
    }
}