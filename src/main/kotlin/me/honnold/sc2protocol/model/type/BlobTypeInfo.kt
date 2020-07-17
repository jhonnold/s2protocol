package me.honnold.sc2protocol.model.type

import me.honnold.sc2protocol.model.Bounds

class BlobTypeInfo(bounds: Bounds) : TypeInfo(TypeMethod.BLOB) {
    override val p: Bounds = bounds

    override fun toString(): String {
        return "('_blob',[${this.p}])"
    }
}