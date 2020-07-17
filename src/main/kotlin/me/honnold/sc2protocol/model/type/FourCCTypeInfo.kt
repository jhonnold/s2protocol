package me.honnold.sc2protocol.model.type

class FourCCTypeInfo : TypeInfo(TypeMethod.FOURCC) {
    override fun toString(): String {
        return "('_fourcc',[])"
    }
}