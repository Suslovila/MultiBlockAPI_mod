package com.suslovila.sus_multi_blocked.api.multiblock.block

import com.suslovila.sus_multi_blocked.api.multiblock.AdditionalData
import com.suslovila.sus_multi_blocked.api.multiblock.MultiStructure
import com.suslovila.sus_multi_blocked.api.multiblock.MultiStructureElement
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import java.util.*

// class of block that is going to fill all the multiStructure when it is created. I recommend just to extend this class, or, if not possible, copy its logic

// recommended: each block type responses for only one structure in mod, because when two multiStructures are placed near, it can result in problems
abstract class MultiStructureBlock<D : AdditionalData, E : MultiStructureElement<D>>(material: Material = Material.iron) :
    BlockContainer(material) {
    abstract val multiStructure: MultiStructure<D, E>
    override fun onNeighborBlockChange(world: World?, x: Int, y: Int, z: Int, neighborBlock: Block?) {
        if (world == null) return
        val innerTile = world.getTileEntity(x, y, z)
        if (innerTile is ITileMultiStructureElement) {
            val isValid = multiStructure.isStillValid(
                world,
                innerTile.getMasterPos(),
                innerTile.getFacing(),
                innerTile.getRotationAngle()
            )
            if (!isValid) {
                multiStructure.deconstruct(
                    world,
                    innerTile.getMasterPos(),
                    innerTile.getFacing(),
                    innerTile.getRotationAngle()
                )
            }
        }
    }

    /**
     *
     */
    override fun getItem(world: World?, x: Int, y: Int, z: Int): Item? {
        if (world == null) return null
        val innerTile = world.getTileEntity(x, y, z)
        if (innerTile is ITileMultiStructureElement) {
            val element = multiStructure.elements[innerTile.getElementIndex()]
            val blockWhenDeconstructed = Block.getBlockFromName(element.storedBlock)
            return ItemStack(blockWhenDeconstructed, element.meta).item
        }
        return null
    }


    override fun onBlockHarvested(
        par1World: World,
        par2: Int,
        par3: Int,
        par4: Int,
        par5: Int,
        par6EntityPlayer: EntityPlayer
    ) {
        if (!par6EntityPlayer.capabilities.isCreativeMode) {
            this.dropBlockAsItem(par1World, par2, par3, par4, par5, 0)
        }
    }


    override fun getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList<ItemStack> {
        val innerTile = world.getTileEntity(x, y, z)
        if (innerTile is ITileMultiStructureElement) {
            val element = multiStructure.elements[innerTile.getElementIndex()]
            val blockWhenDeconstructed = Block.getBlockFromName(element.storedBlock)
            return blockWhenDeconstructed.getDrops(world, x, y, z, element.meta, 0)
        }
        return arrayListOf()
    }
}
