package me.honnold.sc2protocol.model.type

import me.honnold.sc2protocol.model.Field

class StructTypeInfo(fields: List<Field>) : TypeInfo(TypeMethod.STRUCT) {
    override val p: List<Field> = fields

    override fun toString(): String {
        return "('_struct',[${this.p}])"
    }
}