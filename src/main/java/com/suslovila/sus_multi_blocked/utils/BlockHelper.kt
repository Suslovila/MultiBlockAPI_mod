package com.suslovila.sus_multi_blocked.utils

import net.minecraft.block.Block
import net.minecraft.block.material.Material

object BlockHelper {
    fun Block.isAbsoluteAir(): Boolean {
        return this.material === Material.air
    }
}