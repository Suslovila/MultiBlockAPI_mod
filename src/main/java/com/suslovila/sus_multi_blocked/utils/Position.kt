package com.suslovila.sus_multi_blocked.utils

import com.suslovila.sus_multi_blocked.SusMultiBlocked
import io.netty.buffer.ByteBuf
import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sqrt

//This class is used for positions in world ;)
data class Position(val x: Int, val y: Int, val z: Int) {
    val length: Double
        get() {
            return sqrt((x * x + y * y + z * z).toDouble())
        }

    companion object {
        val X_NBT = SusMultiBlocked.prefixAppender.doAndGet("x")
        val Y_NBT = SusMultiBlocked.prefixAppender.doAndGet("y")
        val Z_NBT = SusMultiBlocked.prefixAppender.doAndGet("z")

        fun readFrom(buf: ByteBuf): Position {
            val x = buf.readInt()
            val y = buf.readInt()
            val z = buf.readInt()
            return Position(x, y, z)
        }

        fun readFrom(nbt: NBTTagCompound): Position {
            val x = nbt.getInteger(X_NBT)
            val y = nbt.getInteger(Y_NBT)
            val z = nbt.getInteger(Z_NBT)
            return Position(x, y, z)
        }
    }

    //takes angle in degrees. Only 90 degreeD rotations available
    fun xRot(angle: Int): Position? {
        if (angle.absoluteValue % 45 != 0) return null
        val radians = angle.toDouble() / 180.0 * Math.PI
        val f = Math.cos(radians)
        val f1 = Math.sin(radians)
        val d1 = y * f + z * f1
        val d2 = z * f - y * f1
        return Position(x, d1.roundToInt(), d2.roundToInt())
    }

    fun yRot(angle: Int): Position? {
        if (angle.absoluteValue % 45 != 0) return null
        val radians = angle.toDouble() / 180.0 * Math.PI
        val f = Math.cos(radians)
        val f1 = Math.sin(radians)
        val d0 = x * f + z * f1
        val d2 = z * f - x * f1
        return Position(d0.roundToInt(), y, d2.roundToInt())
    }

    fun zRot(angle: Int): Position? {
        if (angle.absoluteValue % 45 != 0) return null
        val radians = (angle.toDouble()) / 180.0 * Math.PI
        val f = Math.cos(radians)
        val f1 = Math.sin(radians)
        val d0 = x * f + y * f1
        val d1 = y * f - x * f1
        val res = Position(d0.roundToInt(), d1.roundToInt(), z)
        return res
    }

    operator fun plus(position: Position): Position {
        return Position(this.x + position.x, this.y + position.y, this.z + position.z)
    }

    operator fun minus(position: Position): Position {
        return Position(this.x - position.x, this.y - position.y, this.z - position.z)
    }

    fun writeTo(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(y)
        buf.writeInt(z)
    }

    fun writeTo(nbt: NBTTagCompound) {
        nbt.setInteger(X_NBT, x)
        nbt.setInteger(Y_NBT, y)
        nbt.setInteger(Z_NBT, z)
    }
}

fun World.getTile(position: Position): TileEntity? {
    return this.getTileEntity(position.x, position.y, position.z)
}

fun World.getBlock(position: Position): Block? {
    return this.getBlock(position.x, position.y, position.z)
}

fun World.getBlockMetadata(pos: Position): Int {
    return this.getBlockMetadata(pos.x, pos.y, pos.z)
}

fun World.setBlock(pos: Position, block: Block) {
    this.setBlock(pos.x, pos.y, pos.z, block)
}

fun World.setBlock(pos: Position, block: Block, meta: Int, flags: Int) {
    this.setBlock(pos.x, pos.y, pos.z, block, meta, flags)
}

fun World.setTile(position: Position, tileEntity: TileEntity) {
    this.setTileEntity(position.x, position.y, position.z, tileEntity)
}
