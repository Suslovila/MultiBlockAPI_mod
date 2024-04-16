package com.suslovila.sus_multi_blocked.utils

import net.minecraft.util.AxisAlignedBB

object WorldHelper {
    fun boundingBoxFromTwoPos(pos1: Vec3, pos2: Vec3) =
        AxisAlignedBB.getBoundingBox(
            Math.min(pos1.x, pos2.x).toDouble(),
            Math.min(pos1.y, pos2.y).toDouble(),
            Math.min(pos1.z, pos2.z).toDouble(),
            Math.max(pos1.x, pos2.x).toDouble(),
            Math.max(pos1.y, pos2.y).toDouble(),
            Math.max(pos1.z, pos2.z).toDouble()
        )

    fun AxisAlignedBB.forEachBlockPos(operation: (pos: Vec3) -> Unit) {
        for (x in minX.toInt()..maxX.toInt()) {
            for (y in minY.toInt()..maxY.toInt()) {
                for (z in minZ.toInt()..maxZ.toInt()) {
                    operation(Vec3(x, y, z))
                }
            }
        }
    }
}