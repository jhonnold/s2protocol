package me.honnold.sc2protocol.model

class Field(name: String, val typeId: Int, val tag: Int = 0) {
    companion object {
        private const val PARENT_NAME = "__parent"
    }

    val name: String = (if (name.startsWith("m_")) name.substring(2) else name).intern()
    val isNameParent = PARENT_NAME == name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Field

        return other.name == this.name && other.typeId == this.typeId && other.tag == this.tag
    }

    override fun hashCode(): Int =  this.name.hashCode() + this.typeId.hashCode() + this.tag.hashCode()
}