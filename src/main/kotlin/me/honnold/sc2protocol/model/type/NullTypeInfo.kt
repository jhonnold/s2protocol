package me.honnold.sc2protocol.model.type

class NullTypeInfo : TypeInfo(TypeMethod.NULL) {
    override fun toString(): String {
        return "('_null',[])"
    }
}