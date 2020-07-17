package me.honnold.sc2protocol.model.type

import me.honnold.sc2protocol.model.Bounds

class BitArrayTypeInfo(bounds: Bounds) : TypeInfo(TypeMethod.BITARRAY) {
    override val p: Bounds = bounds

    override fun toString(): String {
        return "('_bitarray',[${this.p}])"
    }
}