package com.suslovila.sus_multi_blocked.api.multiblock.block

import com.suslovila.sus_multi_blocked.utils.Position
import net.minecraftforge.common.util.ForgeDirection



// each element of structure needs to remember the sustain of structure when it was built to correctly deconstruct it.
// all your multiStructure blocks must contain tileEntities inside to at least contain info of this class
// check out the simple implementation to understand better
interface ITileMultiStructureElement {
    fun getMasterPos(): Position
    fun getFacing(): ForgeDirection
    fun getRotationAngle(): Int
    fun getElementIndex(): Int

    fun setMasterPos(position: Position)
    fun setFacing(facing : ForgeDirection)
    fun setRotationAngle(angle : Int)
    fun setElementIndex(index : Int)

}