package me.honnold.sc2protocol.model.type

import me.honnold.sc2protocol.model.Bounds
import me.honnold.sc2protocol.model.Field

class ChoiceTypeInfo(bounds: Bounds, fields: List<Field>) : TypeInfo(TypeMethod.CHOICE) {
    override val p: Bounds = bounds
    override val q: List<Field> = fields

    override fun toString(): String {
        return "('_choice',[${this.p}, ${this.q}])"
    }
}