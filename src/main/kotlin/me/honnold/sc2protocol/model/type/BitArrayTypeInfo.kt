package me.honnold.sc2protocol.model.type

class BitArrayTypeInfo(bounds: Bounds) : TypeInfo(TypeMethod.BITARRAY) {
    override val p: Bounds = bounds

    override fun toString(): String {
        return "('_bitarray',[${this.p}])"
    }
}