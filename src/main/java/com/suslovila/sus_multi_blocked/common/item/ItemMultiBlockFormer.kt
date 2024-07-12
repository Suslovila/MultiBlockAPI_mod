package com.suslovila.sus_multi_blocked.common.item

import com.google.gson.stream.JsonWriter
import com.suslovila.sus_multi_blocked.Config
import com.suslovila.sus_multi_blocked.SusMultiBlocked
import com.suslovila.sus_multi_blocked.api.GuiHandler
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getBlockInfo
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getFileName
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getFirstBound
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getMasterPos
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getMode
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getModifiers
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getSecondBound
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.setMasterPos
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.setSecondBound
import com.suslovila.sus_multi_blocked.utils.BlockHelper.isAbsoluteAir
import com.suslovila.sus_multi_blocked.utils.PlayerInteractionHelper.sendChatMessage
import com.suslovila.sus_multi_blocked.utils.SusNBTHelper.getOrCreateTag
import com.suslovila.sus_multi_blocked.utils.Position
import com.suslovila.sus_multi_blocked.utils.WorldHelper.boundingBoxFromTwoPos
import com.suslovila.sus_multi_blocked.utils.WorldHelper.forEachBlockPos
import com.suslovila.sus_multi_blocked.utils.getBlock
import com.suslovila.sus_multi_blocked.utils.getBlockMetadata
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.EnumChatFormatting
import net.minecraft.world.World
import java.io.FileWriter
import java.nio.file.Files
import kotlin.io.path.Path

class ItemMultiBlockFormer : Item() {
    init {
        unlocalizedName = (SusMultiBlocked.prefixAppender.doAndGet(":zone_selector"))
        setTextureName(SusMultiBlocked.MOD_ID + ":zone_selector")
        setMaxStackSize(1)
        creativeTab = SusMultiBlocked.tab
    }


    override fun onItemRightClick(itemStackIn: ItemStack?, worldIn: World?, player: EntityPlayer?): ItemStack? {
        if (itemStackIn == null || worldIn == null || player == null) return itemStackIn
        if (getMode(itemStackIn) == MultiBlockWrapper.MODE.ZONE_SELECTOR) {
            player.openGui(
                SusMultiBlocked.MOD_ID,
                GuiHandler.GUI_MULTIBLOCK_FORMER,
                worldIn,
                player.posX.toInt(),
                player.posY.toInt(),
                player.posZ.toInt()
            )
            return itemStackIn
        }
        return super.onItemRightClick(itemStackIn, worldIn, player)
    }

    override fun onItemUse(
        stack: ItemStack?,
        player: EntityPlayer?,
        world: World?,
        x: Int,
        y: Int,
        z: Int,
        side: Int,
        p_77648_8_: Float,
        p_77648_9_: Float,
        p_77648_10_: Float
    ): Boolean {
        if (stack == null || player == null || world == null) return false

        when (getMode(stack)) {
            MultiBlockWrapper.MODE.BLOCK_CUSTOMIZER -> {
                if (!player.isSneaking) {
                    player.openGui(SusMultiBlocked.MOD_ID, GuiHandler.GUI_MULTIBLOCK_FORMER, world, x, y, z)
                } else {
                    if (!world.isRemote) {
                        val masterPos = Position(x, y, z)
                        setMasterPos(stack, masterPos)
                        player.sendChatMessage("Master position was successfully set to $masterPos")
                    }
                }

            }

            MultiBlockWrapper.MODE.ZONE_SELECTOR -> {
                if (!player.isSneaking) {
                    setSecondBound(stack, x, y, z)
                } else {
                    player.openGui(SusMultiBlocked.MOD_ID, GuiHandler.GUI_MULTIBLOCK_FORMER, world, x, y, z)
                }
            }
        }

        return true
    }


    override fun addInformation(
        stack: ItemStack,
        player: EntityPlayer,
        list: MutableList<Any?>,
        p_77624_4_: Boolean
    ) {

        list.add(EnumChatFormatting.AQUA.toString() + "Current Mode: " + getMode(stack))
        list.add(EnumChatFormatting.GOLD.toString() + "First bound: " + getFirstBound(stack))
        list.add(EnumChatFormatting.GOLD.toString() + "Second bound: " + getSecondBound(stack))

    }
}


fun writeToJsonFromZoneSelector(itemStack: ItemStack, world: World): Boolean {
    val space = boundingBoxFromTwoPos(getFirstBound(itemStack)!!, getSecondBound(itemStack)!!)
    val blocksInfo = getBlockInfo(itemStack)
    val globalModifiers = getModifiers(itemStack.getOrCreateTag())
    val masterPos = getMasterPos(itemStack)!!

    val jsonPath = Files.createDirectories(Path(Config.structureOutputPath)).toString() + "\\" + itemStack.getFileName()
    JsonWriter(FileWriter(jsonPath)).use { writer ->
        writer.beginArray()
        space.forEachBlockPos { blockPos ->
            if (world.getBlock(blockPos)?.isAbsoluteAir() != false) return@forEachBlockPos
            val offset = blockPos - masterPos
            writer.beginObject()
            writer.name("x").value(offset.x)
            writer.name("y").value(offset.y)
            writer.name("z").value(offset.z)
            val name = GameRegistry.findUniqueIdentifierFor(world.getBlock(blockPos)).toString()
            writer.name("storedBlock").value(name)
            writer.name("meta").value(world.getBlockMetadata(blockPos))

            val contains = blocksInfo[blockPos]?.let { blockModifiers ->
                globalModifiers.forEach { globalModifier ->
                    val modifiersWithSuchName = blockModifiers.filter { it.name == globalModifier.name }
                    when (modifiersWithSuchName.size) {
                        0 -> {
                            globalModifier.writeTo(writer)
                        }

                        1 -> {
                            modifiersWithSuchName.first().writeTo(writer)
                        }

                        else -> {
                            println("Error writing structure to json!")
                        }
                    }
                }
            }
            if (contains == null) {
                globalModifiers.forEach { globalModifier ->
                    globalModifier.writeTo(writer)
                }
            }
            writer.endObject()
        }

        writer.endArray()
        writer.close()
    }
    return true
}

fun fillEmptySpaces(world: World, boundingBox: AxisAlignedBB) {
    boundingBox.forEachBlockPos {
        if (world.getBlock(it.x, it.y, it.z).isAbsoluteAir()) {
            world.setBlock(it.x, it.y, it.z, Blocks.grass)
        }
    }
}