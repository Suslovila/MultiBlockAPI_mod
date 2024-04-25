package com.suslovila.sus_multi_blocked.api.multiblock.block

import com.suslovila.sus_multi_blocked.utils.Position
import net.minecraftforge.common.util.ForgeDirection

interface ITileMultiStructureElement {
    fun getMasterPos(): Position
    fun getFacing(): ForgeDirection
    fun getRotationAngle(): Int

    fun setMasterPos(position: Position): Unit
    fun setFacing(facing : ForgeDirection): Unit
    fun setRotationAngle(angle : Int): Unit
}