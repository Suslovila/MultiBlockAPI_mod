package com.suslovila.sus_multi_blocked.utils

import io.netty.buffer.ByteBuf
import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sqrt

//This class is used for positions in world ;)
data class Vec3(val x: Int, val y: Int, val z: Int) {
    val length: Double
        get() {
            return sqrt((x * x + y * y + z * z).toDouble())
        }

    companion object {
        fun readFrom(buf: ByteBuf): Vec3 {
            val x = buf.readInt()
            val y = buf.readInt()
            val z = buf.readInt()
            return Vec3(x, y, z)
        }
        fun readFrom(nbt: NBTTagCompound): Vec3 {
            val x = nbt.getInteger("x")
            val y = nbt.getInteger("y")
            val z = nbt.getInteger("z")
            return Vec3(x, y, z)
        }
    }

    //takes angle in degrees. Only 90 degreeD rotations available
    fun xRot(angle: Int): Vec3? {
        if (angle.absoluteValue % 45 != 0) return null
        val radians = angle.toDouble() / 180.0 * Math.PI
        val f = Math.cos(radians)
        val f1 = Math.sin(radians)
        val d1 = y * f + z * f1
        val d2 = z * f - y * f1
        return Vec3(x, d1.roundToInt(), d2.roundToInt())
    }

    fun yRot(angle: Int): Vec3? {
        if (angle.absoluteValue % 45 != 0) return null
        val radians = angle.toDouble() / 180.0 * Math.PI
        val f = Math.cos(radians)
        val f1 = Math.sin(radians)
        val d0 = x * f + z * f1
        val d2 = z * f - x * f1
        return Vec3(d0.roundToInt(), y, d2.roundToInt())
    }

    fun zRot(angle: Int): Vec3? {
        if (angle.absoluteValue % 45 != 0) return null
        val radians = (angle.toDouble()) / 180.0 * Math.PI
        val f = Math.cos(radians)
        val f1 = Math.sin(radians)
        val d0 = x * f + y * f1
        val d1 = y * f - x * f1
        val res = Vec3(d0.roundToInt(), d1.roundToInt(), z)
        return res
    }

    operator fun plus(position: Vec3): Vec3 {
        return Vec3(this.x + position.x, this.y + position.y, this.z + position.z)
    }

    operator fun minus(position: Vec3): Vec3 {
        return Vec3(this.x - position.x, this.y - position.y, this.z - position.z)
    }

    fun writeTo(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(y)
        buf.writeInt(z)
    }
    fun writeTo(nbt: NBTTagCompound) {
        nbt.setInteger("x", x)
        nbt.setInteger("y", y)
        nbt.setInteger("z", z)
    }
}

fun World.getTile(position: Vec3): TileEntity? {
    return this.getTileEntity(position.x, position.y, position.z)
}

fun World.getBlock(position: Vec3): Block? {
    return this.getBlock(position.x, position.y, position.z)
}

fun World.getBlockMetadata(pos: Vec3): Int {
    return this.getBlockMetadata(pos.x, pos.y, pos.z)
}

fun World.setBlock(pos: Vec3, block: Block) {
    this.setBlock(pos.x, pos.y, pos.z, block)
}

fun World.setBlock(pos: Vec3, block: Block, meta: Int, flags: Int) {
    this.setBlock(pos.x, pos.y, pos.z, block, meta, flags)
}

fun World.setTile(position: Vec3, tileEntity: TileEntity) {
    this.setTileEntity(position.x, position.y, position.z, tileEntity)
}
