package me.honnold.sc2protocol.model

class TypeInfo(val method: TypeMethod, val p: Any? = null, val q: Any? = null) {
    companion object {
        private val METHOD_REGEX = Regex("^\\('(_[a-z]+)'")
        private val BOUNDS_REGEX = Regex("\\(([\\d]+),([\\d]+)\\)")
        private val FIELD_REGEX = Regex("\\('(m_[a-zA-Z]+)',([\\d]+),([\\d]+)\\)")
        private val CHOICE_FIELD_REGEX = Regex("[\\d]+:\\('([\\w]+)',([\\d]+)\\)")
        private val OPTIONAL_REGEX = Regex("\\[([\\d]+)]")

        fun from(src: String): TypeInfo {
            val methodMatch = METHOD_REGEX.find(src) ?: throw IllegalArgumentException("Unable to parse type!")

            val methodGroup = methodMatch.groups[1]
            return when (methodGroup!!.value) {
                "_int" -> {
                    val boundsMatch = BOUNDS_REGEX.find(src, methodGroup.range.last)
                            ?: throw IllegalArgumentException("Invalid line")

                    val offset = boundsMatch.groups[1]!!.value.toLong()
                    val bits = boundsMatch.groups[2]!!.value.toInt()

                    return TypeInfo(if (bits > 32) TypeMethod.LONG else TypeMethod.INT, Bounds(offset, bits))
                }
                "_struct" -> {
                    val fieldsMatch = FIELD_REGEX.findAll(src, methodGroup.range.last)
                    val fields = fieldsMatch.map { Field(it.groups[1]!!.value, it.groups[2]!!.value.toInt(), it.groups[3]!!.value.toInt()) }

                    return TypeInfo(TypeMethod.STRUCT, fields)
                }
                "_choice" -> {
                    val boundsMatch = BOUNDS_REGEX.find(src, methodGroup.range.last)
                            ?: throw IllegalArgumentException("Invalid _choice!")

                    val offset = boundsMatch.groups[1]!!.value.toLong()
                    val bits = boundsMatch.groups[2]!!.value.toInt()
                    val bounds = Bounds(offset, bits)

                    val choiceFieldsMatch = CHOICE_FIELD_REGEX.findAll(src, boundsMatch.range.last)
                    val fields = choiceFieldsMatch.map { Field(it.groups[1]!!.value, it.groups[2]!!.value.toInt()) }

                    return TypeInfo(TypeMethod.CHOICE, bounds, fields)
                }
                "_array" -> {
                    val boundsMatch = BOUNDS_REGEX.find(src, methodGroup.range.last)
                            ?: throw IllegalArgumentException("Invalid _choice!")

                    val offset = boundsMatch.groups[1]!!.value.toLong()
                    val bits = boundsMatch.groups[2]!!.value.toInt()
                    val bounds = Bounds(offset, bits)

                    val typeMatch = Regex("(\\d+)").find(src, boundsMatch.range.last)
                            ?: throw IllegalArgumentException("oops!")
                    val type = typeMatch.groups[1]!!.value.toInt()

                    return TypeInfo(TypeMethod.ARRAY, bounds, type)
                }
                "_bitarray" -> {
                    val boundsMatch = BOUNDS_REGEX.find(src, methodGroup.range.last)
                            ?: throw IllegalArgumentException("Invalid line!")

                    val offset = boundsMatch.groups[1]!!.value.toLong()
                    val bits = boundsMatch.groups[2]!!.value.toInt()

                    return TypeInfo(TypeMethod.BITARRAY, Bounds(offset, bits))
                }
                "_blob" -> {
                    val boundsMatch = BOUNDS_REGEX.find(src, methodGroup.range.last)
                            ?: throw IllegalArgumentException("Invalid line!")

                    val offset = boundsMatch.groups[1]!!.value.toLong()
                    val bits = boundsMatch.groups[2]!!.value.toInt()

                    return TypeInfo(TypeMethod.BLOB, Bounds(offset, bits))
                }
                "_optional" -> {
                    val optionalMatch = OPTIONAL_REGEX.find(src, methodGroup.range.last)
                            ?: throw java.lang.IllegalArgumentException("Invalid line!")

                    val type = optionalMatch.groups[1]!!.value.toInt()

                    return TypeInfo(TypeMethod.OPTIONAL, type)
                }
                "_bool" -> TypeInfo(TypeMethod.BOOL)
                "_fourcc" -> TypeInfo(TypeMethod.FOURCC)
                "_null" -> TypeInfo(TypeMethod.NULL)
                else -> throw IllegalArgumentException("What...")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as TypeInfo

        return other.method == this.method && other.p == this.p && other.q == this.q
    }

    override fun hashCode(): Int = this.method.hashCode() + this.p.hashCode() + this.q.hashCode()
}