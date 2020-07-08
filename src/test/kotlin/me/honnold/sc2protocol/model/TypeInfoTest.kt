package me.honnold.sc2protocol.model

import kotlin.test.Test
import kotlin.test.assertEquals

class TypeInfoTest {
    @Test
    fun int() {
        val src = "('_int',[(0,7)]),"
        val info = TypeInfo.from(src)

        assertEquals(TypeMethod.INT, info.method)
        val expectedBounds = Bounds(0, 7)
        assertEquals(expectedBounds, info.p)
    }

    @Test
    fun int_long() {
        val src = "('_int',[(10,60)]),"
        val info = TypeInfo.from(src)

        assertEquals(TypeMethod.LONG, info.method)
        val expectedBounds = Bounds(10, 60)
        assertEquals(expectedBounds, info.p)
    }

    @Test
    fun struct() {
        val src = "('_struct',[[('m_flags',8,0),('m_major',8,1),('m_minor',8,2),('m_revision',8,3),('m_build',5,4),('m_baseBuild',5,5)]]),"
        val info = TypeInfo.from(src)

        val expectedFields = sequenceOf(
                Field("m_flags", 8, 0),
                Field("m_major", 8, 1),
                Field("m_minor", 8, 2),
                Field("m_revision", 8, 3),
                Field("m_build", 5, 4),
                Field("m_baseBuild", 5, 5)
        )

        assertEquals(TypeMethod.STRUCT, info.method)

        val fields = info.p as Sequence<*>
        assertEquals(expectedFields.toList(), fields.toList())
    }

    @Test
    fun choice() {
        val src = "('_choice',[(0,2),{0:('m_uint6',2),1:('m_uint14',3),2:('m_uint22',4),3:('m_uint32',5)}]),"
        val info = TypeInfo.from(src)

        val expectedBounds = Bounds(0, 2)
        val expectedFields = sequenceOf(
                Field("m_uint6", 2),
                Field("m_uint14", 3),
                Field("m_uint22", 4),
                Field("m_uint32", 5)
        )

        assertEquals(TypeMethod.CHOICE, info.method)
        assertEquals(expectedBounds, info.p)

        val fields = info.q as Sequence<*>
        assertEquals(expectedFields.toList(), fields.toList())
    }

    @Test
    fun array() {
        val src = "('_array',[(0,5),42]),"
        val info = TypeInfo.from(src)

        val expectedBounds = Bounds(0, 5)

        assertEquals(TypeMethod.ARRAY, info.method)
        assertEquals(expectedBounds, info.p)
        assertEquals(42, info.q)
    }

    @Test
    fun bitarray() {
        val src = "('_bitarray',[(0,6)]),"
        val info = TypeInfo.from(src)

        val expectedBounds = Bounds(0, 6)

        assertEquals(TypeMethod.BITARRAY, info.method)
        assertEquals(expectedBounds, info.p)
    }

    @Test
    fun blob() {
        val src = "('_blob',[(0,6)]),"
        val info = TypeInfo.from(src)

        val expectedBounds = Bounds(0, 6)

        assertEquals(TypeMethod.BLOB, info.method)
        assertEquals(expectedBounds, info.p)
    }

    @Test
    fun optional() {
        val src = "('_optional',[18]),"
        val info = TypeInfo.from(src)

        assertEquals(TypeMethod.OPTIONAL, info.method)
        assertEquals(18, info.p)
    }

    @Test
    fun bool() {
        val src = "('_bool',[]),"
        val info = TypeInfo.from(src)

        assertEquals(TypeMethod.BOOL, info.method)
    }

    @Test
    fun fourcc() {
        val src = "('_fourcc',[]),"
        val info = TypeInfo.from(src)

        assertEquals(TypeMethod.FOURCC, info.method)
    }

    @Test
    fun _null() {
        val src = "('_null',[]),"
        val info = TypeInfo.from(src)

        assertEquals(TypeMethod.NULL, info.method)
    }
}