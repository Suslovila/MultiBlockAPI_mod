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


// recommended: block responses for only one structure in mod
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

//    override fun getItemDropped(meta: Int, random: Random?, fortune: Int): Item? = null
//
//    override fun dropBlockAsItem(world: World?, x: Int, y: Int, z: Int, itemIn: ItemStack?) {
//        if (world == null) return
//        val innerTile = world.getTileEntity(x, y, z)
//        // there is no tile entity there for that moment
//        if (innerTile is ITileMultiStructureElement) {
////            val fillingBlock = multiStructure.elements[innerTile.getElementIndex()].additionalData.fillingBlock
////            val itemBlock = ItemStack(fillingBlock)
////            val f = 0.7f
////            val d0: Double = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
////            val d1: Double = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
////            val d2: Double = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
////            val entityItem = EntityItem(world, x.toDouble() + d0, y.toDouble() + d1, z.toDouble() + d2, itemBlock)
////            entityItem.delayBeforeCanPickup = 10
////            world.spawnEntityInWorld(entityItem)
//        }
//    }

//    fun getAllDrops(world: World?, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): ArrayList<ItemStack> {
//        if (world == null) return arrayListOf()
//        val innerTile = world.getTileEntity(x, y, z)
//        if (innerTile is ITileMultiStructureElement) {
//            val element = multiStructure.elements[innerTile.getElementIndex()]
//            val blockWhenDeconstructed = Block.getBlockFromName(element.storedBlock)
//            val itemBlock = ItemStack(blockWhenDeconstructed, element.meta)
//            return arrayListOf(itemBlock)
//        }
//        return arrayListOf()
//    }
//
//    override fun breakBlock(world: World?, x: Int, y: Int, z: Int, blockBroken: Block?, meta: Int) {
//        if (world == null) return
//        val innerTile = world.getTileEntity(x, y, z)
//        if (innerTile is ITileMultiStructureElement) {
//            val element = multiStructure.elements[innerTile.getElementIndex()]
//            val fillingBlock = element.additionalData.fillingBlock
//            val itemBlock = ItemStack(fillingBlock, element.meta)
//            val f = 0.7f
//            val d0: Double = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
//            val d1: Double = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
//            val d2: Double = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
//            val entityItem = EntityItem(world, x.toDouble() + d0, y.toDouble() + d1, z.toDouble() + d2, itemBlock)
//            entityItem.delayBeforeCanPickup = 10
//            world.spawnEntityInWorld(entityItem)
//        }
//        super.breakBlock(world, x, y, z, blockBroken, meta)
//    }

//    override fun harvestBlock(world: World, player: EntityPlayer?, x: Int, y: Int, z: Int, meta: Int) {
//        super.harvestBlock(world, player, x, y, z, meta)
//        world.setBlockToAir(x, y, z)
//    }

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

//    override fun onBlockHarvested(world: World, x: Int, y: Int, z: Int, meta: Int, player: EntityPlayer?) {
//        val drops = getAllDrops(world, x, y, z, meta, 0)
//        for (stack in drops) {
//            world.spawnEntityInWorld(EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack))
//        }
//    }


//    override fun harvestBlock(world: World, player: EntityPlayer?, x: Int, y: Int, z: Int, meta: Int) {
//        val innerTile = world.getTileEntity(x, y, z)
//        if (innerTile is ITileMultiStructureElement) {
//            val isValid = multiStructure.isStillValid(world, innerTile.getMasterPos(), innerTile.getFacing(), innerTile.getRotationAngle())
//            if(!isValid) {
//                multiStructure.deconstruct(world, innerTile.getMasterPos(), innerTile.getFacing(), innerTile.getRotationAngle())
//            }
//        }
//    }


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
            return arrayListOf(ItemStack(blockWhenDeconstructed, 1, element.meta))
        }
        return arrayListOf()
    }
}
