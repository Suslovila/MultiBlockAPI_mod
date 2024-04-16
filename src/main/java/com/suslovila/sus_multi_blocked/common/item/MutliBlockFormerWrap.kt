package com.suslovila.sus_multi_blocked.common.item

import com.google.gson.stream.JsonWriter
import com.suslovila.sus_multi_blocked.SusMultiBlocked
import com.suslovila.sus_multi_blocked.utils.SerialiseType
import com.suslovila.sus_multi_blocked.utils.SusNBTHelper.getOrCreateTag
import com.suslovila.sus_multi_blocked.utils.SusVec3
import com.suslovila.sus_multi_blocked.utils.Vec3
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

    val modifiersMaxAmount = 10

    enum class MODE {
        BLOCK_CUSTOMIZER,
        ZONE_SELECTOR,
    }

    fun ItemStack.setDefaultSettingsForBlock(x: Int, y: Int, z: Int) {

    }


    fun ItemStack.setFirstBound(x: Int, y: Int, z: Int) {
        this.getOrCreateTag().setIntArray(FIRST_BOUND_NAME, intArrayOf(x, y, z))
    }

    fun ItemStack.setSecondBound(x: Int, y: Int, z: Int) {
        this.getOrCreateTag().setIntArray(SECOND_BOUND_NAME, intArrayOf(x, y, z))
    }

    fun ItemStack.getFirstBound(): Vec3? {
        val bound = this.getOrCreateTag().getIntArray(FIRST_BOUND_NAME)
        return if (bound.size == 3) SusVec3.vec3FromCollection(bound.asList()) else null
    }

    fun ItemStack.getSecondBound(): Vec3? {
        val bound = this.getOrCreateTag().getIntArray(SECOND_BOUND_NAME)
        return if (bound.size == 3) SusVec3.vec3FromCollection(bound.asList()) else null

    }

    fun ItemStack.getMode(): MODE {
        val tag = this.getOrCreateTag()
        if (!tag.hasKey(MODE_NAME)) {
            tag.setInteger(MODE_NAME, 0)
        }
        val ordinal = tag.getInteger(MODE_NAME)
        val mode = MODE.values()[ordinal]
        return mode
    }

    fun NBTTagCompound.addModifier(modifier: Modifier) {
        getTagListWithModifiers().let { tagList ->
            val newModifierTag = NBTTagCompound()
            modifier.writeTo(newModifierTag)
        }
    }

    fun NBTTagCompound.setModifiers(modifiers: ArrayList<Modifier>) {
        print("ULALALA")
        getTagListWithModifiers().let { tagList ->
            tagList.tagList.clear()
            for (modifier in modifiers) {
                val tagForSingleModifier = NBTTagCompound()
                modifier.writeTo(tagForSingleModifier)
                tagList.appendTag(tagForSingleModifier)
            }
        }
    }

    fun NBTTagCompound.getModifier(requiredName: String): Modifier? {
        val tagWithModifiers = getTagListWithModifiers() ?: return null
        for (i in 0..tagWithModifiers.tagCount()) {
            val tagWithSingleModifier = tagWithModifiers.getCompoundTagAt(i)
            val modifier = Modifier.readFrom(tagWithSingleModifier)
            if (modifier.name == requiredName) {
                return modifier
            }
        }
        return null
    }

    fun ItemStack.deleteGlobalModifier(requiredName: String) {
        getOrCreateTag().getTagListWithModifiers()?.let {
            for (i in 0..it.tagCount()) {
                val tagWithSingleModifier = it.getCompoundTagAt(i)
                val modifier = Modifier.readFrom(tagWithSingleModifier)
                if (modifier.name == requiredName) {
                    it.removeTag(i)
                }
            }
        }
//        getTagListWithBlocksInfo()?.let { blockInfo ->
//            for (blockInfoIndex in 0..blockInfo.tagCount()) {
//                val tagWithSingleBlock = blockInfo.getCompoundTagAt(blockInfoIndex)
//                getTagListWithModifiers(tagWithSingleBlock)?.let { singleBlockModifiers ->
//                    var modifierIndex = 0
//                    while (modifierIndex < singleBlockModifiers.tagCount()) {
//                        val tagWithModifier = singleBlockModifiers.getCompoundTagAt(modifierIndex)
//                        val modifier = Modifier.readFrom(tagWithModifier)
//                        if (modifier.name == requiredName) {
//                            singleBlockModifiers.removeTag(modifierIndex)
//                        } else {
//                            modifierIndex++
//                        }
//                    }
//                }
//            }
//        }
    }

    fun NBTTagCompound.getModifiers(): ArrayList<Modifier> {
        val modifiers = arrayListOf<Modifier>()
        val tagWithModifiers = getTagListWithModifiers() ?: return ArrayList()
        for (i in 0 until tagWithModifiers.tagCount()) {
            val tagWithSingleModifier = tagWithModifiers.getCompoundTagAt(i)
            modifiers.add(Modifier.readFrom(tagWithSingleModifier))
        }
        return modifiers
    }

    fun NBTTagCompound.hasModifierNamed(name: String): Boolean {
        val tagWithModifiers = getTagListWithModifiers() ?: return false
        for (i in 0..tagWithModifiers.tagCount()) {
            val tagWithSingleModifier = tagWithModifiers.getCompoundTagAt(i)
            val modifier = Modifier.readFrom(tagWithSingleModifier)
            if (modifier.name == name) return true
        }
        return false
    }

    fun NBTTagCompound.initModifiers() {
        if (getTagListWithModifiers() != null) return
        val emptyList = NBTTagList()
//        this.setBoolean()
//        this.setTag("343", null)
//        emptyList.tagList = arrayOfNulls<NBTBase>(modifiersMaxAmount).toList()
//        this.setTag(MODIFIERS_NAME, emptyList) // for global modif
//        this.setTag(BLOCK_MODIFIERS_NAME, NBTTagList()) // for all blockPoses

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

    fun ItemStack.getBlockInfo(): MutableMap<Vec3, ArrayList<Modifier>> {
        getTagListWithBlocksInfo().let { tagWithBlockInfo ->
            val info = mutableMapOf<Vec3, ArrayList<Modifier>>()

            for (blockInfoIndex in 0..tagWithBlockInfo.tagCount()) {
                val tagWithSingleBlock = tagWithBlockInfo.getCompoundTagAt(blockInfoIndex)

                val modifiers = tagWithSingleBlock.getModifiers() ?: continue
                val pos = Vec3(
                    tagWithSingleBlock.getInteger("x"),
                    tagWithSingleBlock.getInteger("y"),
                    tagWithSingleBlock.getInteger("z")
                )
                info[pos] = modifiers
            }
            return info
        }
    }


    fun ItemStack.getTagListWithBlocksInfo(): NBTTagList {
        val rootTag = getOrCreateTag()
        if (!rootTag.hasKey(BLOCK_MODIFIERS_NAME)) rootTag.setTag(BLOCK_MODIFIERS_NAME, NBTTagList())
        return rootTag.getTagList(BLOCK_MODIFIERS_NAME, TAG_COMPOUND)
    }

    fun NBTTagCompound.getTagListWithModifiers(): NBTTagList {
        if (!this.hasKey(MODIFIERS_NAME)) this.setTag(MODIFIERS_NAME, NBTTagList())
        return this.getTagList(MODIFIERS_NAME, TAG_COMPOUND)
    }

    fun ItemStack.setBlockInfo(pos: Vec3, modifiers: ArrayList<Modifier>) {
        val result = this.getTagListWithBlocksInfo().tagList.firstOrNull { tag ->
            if (tag !is NBTTagCompound) return@firstOrNull false
            return@firstOrNull (tag.getInteger("x") == pos.x &&
                    tag.getInteger("y") == pos.y &&
                    tag.getInteger("z") == pos.z)
        }?.also { foundTag ->
            (foundTag as NBTTagCompound).setModifiers(modifiers)
        }
        if (result == null) {
            val nbtForBlock = NBTTagCompound()
            pos.writeTo(nbtForBlock)
            nbtForBlock.setModifiers(modifiers)
            this.getTagListWithBlocksInfo().appendTag(nbtForBlock)
        }
    }

    fun ItemStack.setMasterPos(pos: Vec3) {
        getOrCreateTag().setIntArray(MASTER_BLOCK_POS, intArrayOf(pos.x, pos.y, pos.z))
    }
    fun ItemStack.getMasterPos() : Vec3? {
        if(!this.getOrCreateTag().hasKey(MASTER_BLOCK_POS)) return null
        val array = getOrCreateTag().getIntArray(MASTER_BLOCK_POS)
        return Vec3(array[0], array[1], array[2])
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

fun Any?.ifNull(expression: () -> Unit) {
    if (this == null) expression()
}
