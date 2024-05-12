package com.suslovila.sus_multi_blocked.api.multiblock.block

import com.suslovila.sus_multi_blocked.api.multiblock.AdditionalData
import com.suslovila.sus_multi_blocked.api.multiblock.MultiStructure
import com.suslovila.sus_multi_blocked.api.multiblock.MultiStructureElement
import net.minecraft.block.Block
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

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
}
