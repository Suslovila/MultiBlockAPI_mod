package com.suslovila.sus_multi_blocked.common.item

import com.google.gson.stream.JsonWriter
import com.suslovila.sus_multi_blocked.SusMultiBlocked
import com.suslovila.sus_multi_blocked.utils.SerialiseType
import com.suslovila.sus_multi_blocked.utils.SusNBTHelper.getOrCreateTag
import com.suslovila.sus_multi_blocked.utils.SusVec3
import com.suslovila.sus_multi_blocked.utils.Position
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

object MultiBlockWrapper {

    val MODE_NAME = SusMultiBlocked.prefixAppender.doAndGet("mode")
    val FIRST_BOUND_NAME = SusMultiBlocked.prefixAppender.doAndGet("firstBound")
    val SECOND_BOUND_NAME = SusMultiBlocked.prefixAppender.doAndGet("secondBound")
    val FILE_NAME = SusMultiBlocked.prefixAppender.doAndGet("file_name")
    val MASTER_BLOCK_POS = SusMultiBlocked.prefixAppender.doAndGet("master_block_pos")

    const val TAG_COMPOUND = 10

    val MODIFIERS_NAME = SusMultiBlocked.prefixAppender.doAndGet("global_modifiers")
    val BLOCK_MODIFIERS_NAME = SusMultiBlocked.prefixAppender.doAndGet("single_modifiers")


    enum class MODE {
        BLOCK_CUSTOMIZER,
        ZONE_SELECTOR,
    }

    fun setFirstBound(stack: ItemStack, x: Int, y: Int, z: Int) {
        stack.getOrCreateTag().setIntArray(FIRST_BOUND_NAME, intArrayOf(x, y, z))
    }

    fun setSecondBound(stack: ItemStack, x: Int, y: Int, z: Int) {
        stack.getOrCreateTag().setIntArray(SECOND_BOUND_NAME, intArrayOf(x, y, z))
    }

    fun getFirstBound(stack: ItemStack): Position? {
        val bound = stack.getOrCreateTag().getIntArray(FIRST_BOUND_NAME)
        return if (bound.size == 3) SusVec3.vec3FromCollection(bound.asList()) else null
    }

    fun getSecondBound(stack: ItemStack): Position? {
        val bound = stack.getOrCreateTag().getIntArray(SECOND_BOUND_NAME)
        return if (bound.size == 3) SusVec3.vec3FromCollection(bound.asList()) else null

    }

    fun getMode(itemStack: ItemStack): MODE {
        val tag = itemStack.getOrCreateTag()
        if (!tag.hasKey(MODE_NAME)) {
            tag.setInteger(MODE_NAME, 0)
        }
        val ordinal = tag.getInteger(MODE_NAME)
        val mode = MODE.values()[ordinal]
        return mode
    }


    fun setModifiers(nbt: NBTTagCompound, modifiers: ArrayList<Modifier>) {
        getTagListWithModifiers(nbt).let { tagList ->
            tagList.tagList.clear()
            for (modifier in modifiers) {
                val tagForSingleModifier = NBTTagCompound()
                modifier.writeTo(tagForSingleModifier)
                tagList.appendTag(tagForSingleModifier)
            }
        }
    }


    fun getModifiers(nbt: NBTTagCompound): ArrayList<Modifier> {
        val modifiers = arrayListOf<Modifier>()
        val tagWithModifiers = getTagListWithModifiers(nbt)
        for (i in 0 until tagWithModifiers.tagCount()) {
            val tagWithSingleModifier = tagWithModifiers.getCompoundTagAt(i)
            modifiers.add(Modifier.readFrom(tagWithSingleModifier))
        }
        return modifiers
    }


    fun ItemStack.getFileName(): String {
        if (!this.getOrCreateTag().hasKey(FILE_NAME)) {
            this.getOrCreateTag().setString(FILE_NAME, "default_structure_name.json")
        }
        return this.getOrCreateTag().getString(FILE_NAME)
    }

    fun ItemStack.setFileName(name: String) {
        this.getOrCreateTag().setString(FILE_NAME, name)
    }

    fun getBlockInfo(stack: ItemStack): MutableMap<Position, ArrayList<Modifier>> {
        getTagListWithBlocksInfo(stack).let { tagWithBlockInfo ->
            val info = mutableMapOf<Position, ArrayList<Modifier>>()

            for (blockInfoIndex in 0..tagWithBlockInfo.tagCount()) {
                val tagWithSingleBlock = tagWithBlockInfo.getCompoundTagAt(blockInfoIndex)

                val modifiers = getModifiers(tagWithSingleBlock)
                val pos = Position.readFrom(tagWithSingleBlock)

                info[pos] = modifiers
            }
            return info
        }
    }


    fun getTagListWithBlocksInfo(stack: ItemStack): NBTTagList {
        val rootTag = stack.getOrCreateTag()
        if (!rootTag.hasKey(BLOCK_MODIFIERS_NAME)) rootTag.setTag(BLOCK_MODIFIERS_NAME, NBTTagList())
        return rootTag.getTagList(BLOCK_MODIFIERS_NAME, TAG_COMPOUND)
    }

    fun getTagListWithModifiers(nbt: NBTTagCompound): NBTTagList {
        if (!nbt.hasKey(MODIFIERS_NAME)) nbt.setTag(MODIFIERS_NAME, NBTTagList())
        return nbt.getTagList(MODIFIERS_NAME, TAG_COMPOUND)
    }

    fun ItemStack.setBlockInfo(pos: Position, modifiers: ArrayList<Modifier>) {
        val result = getTagListWithBlocksInfo(this).tagList.firstOrNull { tag ->
            if (tag !is NBTTagCompound) return@firstOrNull false
            return@firstOrNull (Position.readFrom(tag) == pos)
        }?.also { foundTag ->
            setModifiers((foundTag as NBTTagCompound), modifiers)
        }
        if (result == null) {
            val nbtForBlock = NBTTagCompound()
            pos.writeTo(nbtForBlock)
            setModifiers(nbtForBlock, modifiers)
            getTagListWithBlocksInfo(this).appendTag(nbtForBlock)
        }
    }

    fun setMasterPos(stack: ItemStack, pos: Position) {
        stack.getOrCreateTag().setIntArray(MASTER_BLOCK_POS, intArrayOf(pos.x, pos.y, pos.z))
    }
    fun getMasterPos(stack: ItemStack) : Position? {
        if(!stack.getOrCreateTag().hasKey(MASTER_BLOCK_POS)) return null
        val array = stack.getOrCreateTag().getIntArray(MASTER_BLOCK_POS)
        return Position(array[0], array[1], array[2])
    }
}

class Modifier(
    val name: String,
    val type: SerialiseType,
    var value: Any

) {
    override fun equals(other: Any?): Boolean {
        return other is Modifier &&
                other.name == this.name &&
                other.value == this.value &&
                other.type == type
    }

    override fun toString(): String {
        return "$name, value: $value"
    }

    companion object {
        val VALUE_KEY = SusMultiBlocked.prefixAppender.doAndGet("modifier_value")
        val NAME_KEY = SusMultiBlocked.prefixAppender.doAndGet("modifier_name")
        val TYPE_KEY = SusMultiBlocked.prefixAppender.doAndGet("modifier_type")

        fun readFrom(tag: NBTTagCompound): Modifier {
            val stringType = tag.getString(TYPE_KEY)
            val type = SerialiseType.valueOf(stringType)
            return Modifier(
                tag.getString(NAME_KEY),
                type,
                type.deSerialiseNbt(tag, VALUE_KEY)
                    ?: throw Exception("error reading nbt tag: $tag with key: $VALUE_KEY"),

                )
        }

        fun readFrom(buf: ByteBuf): Modifier {
            val name = ByteBufUtils.readUTF8String(buf)
            val typeId = buf.readInt()
            val type = SerialiseType.values()[typeId]
            return Modifier(
                name,
                type,
                type.deSerialiseBuf(buf)
                    ?: throw Exception("error reading nbt tag: $buf with key: $VALUE_KEY"),

                )
        }
    }

    fun writeTo(tag: NBTTagCompound) {
        tag.setString(NAME_KEY, name)
        tag.setString(TYPE_KEY, type.toString())
        type.serialiseNbt(tag, VALUE_KEY, value)
    }


    fun writeTo(writer: JsonWriter) {
        type.serialiseJson(writer, name, value)
    }

    fun writeTo(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, name)
        buf.writeInt(type.ordinal)
        type.serialiseBuf(buf, value)
    }


    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

