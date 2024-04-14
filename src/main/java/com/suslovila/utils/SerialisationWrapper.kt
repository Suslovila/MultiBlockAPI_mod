package com.suslovila.utils

import com.google.gson.stream.JsonWriter
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound

//object SerialisationWrapper {
//
//    class Serializer(
//        val serialiseNbt: (NBTTagCompound, String, Any) -> Unit,
//        val deSerialiseNbt: (NBTTagCompound, String) -> (Any?),
//        val serialiseJson: (JsonWriter, String, Any) -> Unit,
//        val cast: (String) -> Any?,
//    ) {
//        fun validate(value: String): Boolean {
//            return cast(value) != null
//        }
//    }
//
//    val serialisers = mutableMapOf(
//        "int" to Serializer(
//            serialiseNbt = { nbt, key, expectedInt ->
//                if (expectedInt !is Int) throw Exception("error writing nbt")
//                nbt.setInteger(key, expectedInt)
//            },
//            deSerialiseNbt = { nbt, key ->
//                if (!nbt.hasKey(key)) null
//                else nbt.getInteger(key)
//            },
//            cast = { stringValue ->
//                try {
//                    stringValue.toInt()
//                } catch (exception: Exception) {
//                    null
//                }
//            },
//            serialiseJson = { writer, name, expectedInt ->
//                if (expectedInt !is Int) throw Exception("error writing json")
//                writer.name(name).value(expectedInt)
//            }
//        ),
//
//
//        "string" to Serializer(
//            serialiseNbt = { nbt, key, expectedString ->
//                if (expectedString !is String) throw Exception("error writing nbt")
//                nbt.setString(key, expectedString)
//            },
//            deSerialiseNbt = { nbt, key ->
//                if (!nbt.hasKey(key)) null
//                else nbt.getString(key)
//            },
//            cast = { stringValue ->
//                stringValue
//            },
//            serialiseJson = { writer, name, expectedString ->
//                if (expectedString !is String) throw Exception("error writing json")
//                writer.name(name).value(expectedString)
//            }
//        ),
//
//
//        "double" to Serializer(
//            serialiseNbt = { nbt, key, expectedDouble ->
//                if (expectedDouble !is Double) throw Exception("error writing nbt")
//                nbt.setDouble(key, expectedDouble)
//            },
//            deSerialiseNbt = { nbt, key ->
//                if (!nbt.hasKey(key)) null
//                else nbt.getDouble(key)
//            },
//            cast = { stringValue ->
//                try {
//                    stringValue.toDouble()
//                } catch (exception: Exception) {
//                    null
//                }
//            },
//            serialiseJson = { writer, name, expectedDouble ->
//                if (expectedDouble !is Double) throw Exception("error writing json")
//                writer.name(name).value(expectedDouble)
//            }
//        ),
//
//
//        "boolean" to Serializer(
//            { nbt, key, expectedBoolean ->
//                if (expectedBoolean !is Boolean) throw Exception("error writing nbt")
//                nbt.setBoolean(key, expectedBoolean)
//            },
//            { nbt, key ->
//                if (!nbt.hasKey(key)) null
//                else nbt.getBoolean(key)
//            },
//            cast = { stringValue ->
//                try {
//                    stringValue.toBoolean()
//                } catch (exception: Exception) {
//                    null
//                }
//            },
//            serialiseJson = { writer, name, expectedBoolean ->
//                if (expectedBoolean !is Boolean) throw Exception("error writing json")
//                writer.name(name).value(expectedBoolean)
//            }
//        ),
//
////        "nbt" to Serializer(
////            { nbt, key, expectedNbt ->
////                if (expectedNbt !is NBTBase) throw Exception("error writing nbt")
////                nbt.setTag(key, expectedNbt)
////            },
////            { nbt, key ->
////                if (!nbt.hasKey(key)) null
////                else nbt.getTag(key)
////            },
////            cast = { stringValue ->
////                try {
////                    val tag = NBTTagCompound()
////                    tag.writ
////                } catch (exception: Exception) {
////                    null
////                }
////            }
////        ),
//    )
//
//    fun writeTo(tag: NBTTagCompound, key: String, value: Any, type: String): Unit {
//        serialisers[type]?.serialiseNbt?.let { it(tag, key, value) }
//    }
//
//    fun readFrom(tag: NBTTagCompound, key: String, type: String): Any? {
//        return serialisers[type]?.deSerialiseNbt?.let { it(tag, key) }
//    }
//
//    fun writeTo(tag: JsonWriter, name: String, value: Any, type: String): Unit {
//        serialisers[type]?.serialiseJson?.let { it(tag, name, value) }
//    }
//
//
//    val types: MutableSet<String>
//        get() {
//            return serialisers.keys
//        }
//}

enum class SerialiseType(
    val serialiseNbt: (NBTTagCompound, String, Any) -> Unit,
    val deSerialiseNbt: (NBTTagCompound, String) -> (Any?),
    val serialiseBuf: (ByteBuf, Any) -> Unit,
    val deSerialiseBuf: (ByteBuf) -> (Any?),
    val serialiseJson: (JsonWriter, String, Any) -> Unit,
    val cast: (String) -> Any?,
) {
    INTEGER(
        serialiseNbt = { nbt, key, expectedInt ->
            if (expectedInt !is Int) throw Exception("error writing nbt")
            nbt.setInteger(key, expectedInt)
        },
        deSerialiseNbt = { nbt, key ->
            if (!nbt.hasKey(key)) null
            else nbt.getInteger(key)
        },
        cast = { stringValue ->
            try {
                stringValue.toInt()
            } catch (exception: Exception) {
                null
            }
        },
        serialiseJson = { writer, name, expectedInt ->
            if (expectedInt !is Int) throw Exception("error writing json")
            writer.name(name).value(expectedInt)
        },
        serialiseBuf = { buf, expectedInt ->
            if (expectedInt !is Int) throw Exception("error writing buf")
            buf.writeInt(expectedInt)
        },
        deSerialiseBuf = { buf ->
            buf.readInt()
        }
    ),

    STRING(
        serialiseNbt = { nbt, key, expectedString ->
            if (expectedString !is String) throw Exception("error writing nbt")
            nbt.setString(key, expectedString)
        },
        deSerialiseNbt = { nbt, key ->
            if (!nbt.hasKey(key)) null
            else nbt.getString(key)
        },
        cast = { stringValue ->
            stringValue
        },
        serialiseJson = { writer, name, expectedString ->
            if (expectedString !is String) throw Exception("error writing json")
            writer.name(name).value(expectedString)
        },
        serialiseBuf = { buf, expectedString ->
            if (expectedString !is String) throw Exception("error writing nbt")
            ByteBufUtils.writeUTF8String(buf, expectedString)
        },
        deSerialiseBuf = { buf ->
            ByteBufUtils.readUTF8String(buf)
        }
    ),
    DOUBLE(
        serialiseNbt = { nbt, key, expectedDouble ->
            if (expectedDouble !is Double) throw Exception("error writing buf")
            nbt.setDouble(key, expectedDouble)
        },
        deSerialiseNbt = { nbt, key ->
            if (!nbt.hasKey(key)) null
            else nbt.getDouble(key)
        },
        cast = { stringValue ->
            try {
                stringValue.toDouble()
            } catch (exception: Exception) {
                null
            }
        },
        serialiseJson = { writer, name, expectedDouble ->
            if (expectedDouble !is Double) throw Exception("error writing json")
            writer.name(name).value(expectedDouble)
        },
        serialiseBuf = { buf, expectedDouble ->
            if (expectedDouble !is Double) throw Exception("error writing buf")
            buf.writeDouble(expectedDouble)
        },
        deSerialiseBuf = { buf ->
            buf.readDouble()
        }
    ),
    BOOLEAN(

        serialiseNbt = { nbt, key, expectedBoolean ->
            if (expectedBoolean !is Boolean) throw Exception("error writing nbt")
            nbt.setBoolean(key, expectedBoolean)
        },
        deSerialiseNbt = { nbt, key ->
            if (!nbt.hasKey(key)) null
            else nbt.getBoolean(key)
        },
        cast = { stringValue ->
            stringValue.toBooleanOrNull()
        },
        serialiseJson = { writer, name, expectedBoolean ->
            if (expectedBoolean !is Boolean) throw Exception("error writing json")
            writer.name(name).value(expectedBoolean)
        },
        serialiseBuf = { buf, expectedBoolean ->
            if (expectedBoolean !is Boolean) throw Exception("error writing buf")
            buf.writeBoolean(expectedBoolean)
        },
        deSerialiseBuf = { buf ->
            buf.readBoolean()
        }
    )
}

fun String.toBooleanOrNull(): Boolean? =
    when (this.lowercase()) {
        "true" -> true
        "false" -> false
        else -> null
    }
