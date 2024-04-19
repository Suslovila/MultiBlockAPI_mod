package com.suslovila.sus_multi_blocked.utils

import net.minecraft.util.AxisAlignedBB

object WorldHelper {
    fun boundingBoxFromTwoPos(pos1: Position, pos2: Position) =
        AxisAlignedBB.getBoundingBox(
            Math.min(pos1.x, pos2.x).toDouble(),
            Math.min(pos1.y, pos2.y).toDouble(),
            Math.min(pos1.z, pos2.z).toDouble(),
            Math.max(pos1.x, pos2.x).toDouble(),
            Math.max(pos1.y, pos2.y).toDouble(),
            Math.max(pos1.z, pos2.z).toDouble()
        )

    fun AxisAlignedBB.forEachBlockPos(operation: (pos: Position) -> Unit) {
        for (x in minX.toInt()..maxX.toInt()) {
            for (y in minY.toInt()..maxY.toInt()) {
                for (z in minZ.toInt()..maxZ.toInt()) {
                    operation(Position(x, y, z))
                }
            }
        }
    }
}