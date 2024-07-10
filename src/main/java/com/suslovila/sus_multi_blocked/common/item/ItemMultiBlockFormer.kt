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
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.JsonToNBT
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
        if (itemStackIn.getMode() == MultiBlockWrapper.MODE.ZONE_SELECTOR) {
            player.openGui(
                SusMultiBlocked.MOD_ID,
                GuiHandler.GUI_MULTIBLOCK_FORMER,
                worldIn,
                player.posX.toInt(),
                player.posY.toInt(),
                player.posZ.toInt()
            )
            return itemStackIn;
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

        when (stack.getMode()) {
            MultiBlockWrapper.MODE.BLOCK_CUSTOMIZER -> {
                if (!player.isSneaking) {
                    player.openGui(SusMultiBlocked.MOD_ID, GuiHandler.GUI_MULTIBLOCK_FORMER, world, x, y, z)
                } else {
                    if (!world.isRemote) {
                        val masterPos = Position(x, y, z)
                        stack.setMasterPos(masterPos)
                        player.sendChatMessage("Master position was successfully set to $masterPos")
                    }
                }

            }

            MultiBlockWrapper.MODE.ZONE_SELECTOR -> {
                if (!player.isSneaking) {
                    stack.setSecondBound(x, y, z)
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

        list.add(EnumChatFormatting.AQUA.toString() + "Current Mode: " + stack.getMode())
        list.add(EnumChatFormatting.GOLD.toString() + "First bound: " + stack.getFirstBound())
        list.add(EnumChatFormatting.GOLD.toString() + "Second bound: " + stack.getSecondBound())

    }

    override fun onUpdate(stack: ItemStack?, worldIn: World?, entityIn: Entity?, p_77663_4_: Int, p_77663_5_: Boolean) {
        super.onUpdate(stack, worldIn, entityIn, p_77663_4_, p_77663_5_)
        if (worldIn == null || entityIn == null || stack == null) return
        if (!worldIn.isRemote) {
            val f = 4;
        }
    }
}


fun writeToJsonFromZoneSelector(itemStack: ItemStack, world: World): Boolean {
//    if (itemStack.getFirstBound() == null || itemStack.getSecondBound() == null) return false
    val space = boundingBoxFromTwoPos(itemStack.getFirstBound()!!, itemStack.getSecondBound()!!)
    val blocksInfo = itemStack.getBlockInfo()
    val globalModifiers = itemStack.getOrCreateTag().getModifiers()
    val masterPos = itemStack.getMasterPos()!!
    val masterPosAsClassicVec3 = net.minecraft.util.Vec3.createVectorHelper(
        masterPos.x.toDouble(),
        masterPos.y.toDouble(),
        masterPos.z.toDouble()
    )

    val jsonPath = Files.createDirectories(Path(Config.structureOutputPath)).toString() + "\\" + itemStack.getFileName()
//    val jsonPath = Config.structureOutputPath + itemStack.getFileName()
//    val directory = File(jsonPath)
//    if (!directory.exists()) {
//        directory.mkdirs()
//    }
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