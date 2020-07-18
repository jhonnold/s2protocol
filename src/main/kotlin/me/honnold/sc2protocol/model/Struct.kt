package me.honnold.sc2protocol.model

class Struct(val struct: Map<String, Any?>) {
    inline operator fun <reified T> get(key: String): T? {
        val value = this.struct[key] ?: return null

        if (value !is T) throw ClassCastException("Value is not of type ${T::class.simpleName}, it is ${value::class.simpleName}")
        return value
    }

    override fun toString(): String {
        return struct.toString()
    }
}