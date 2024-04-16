package com.suslovila.sus_multi_blocked.utils

import com.suslovila.sus_multi_blocked.SusMultiBlocked
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11

object RotationHelper {
    var TAG_FACING = SusMultiBlocked.MOD_ID + "_facing"

    fun readRotation(tag: NBTTagCompound): ForgeDirection? {
        return ForgeDirection.getOrientation(tag.getByte(TAG_FACING).toInt())
    }

    fun writeRotation(tag: NBTTagCompound, facing: ForgeDirection) {
        tag.setByte(TAG_FACING, facing.ordinal.toByte())
    }

    //NOTE THAT ROTATION FUNCTIONS expect "UP" as default facing!
    fun rotateSystemFromOrientation(facing: ForgeDirection) {
        when (facing) {
            ForgeDirection.DOWN -> {
                GL11.glRotatef(180f, 1f, 0f, 0f)
            }

            ForgeDirection.SOUTH -> {
                GL11.glRotatef(90f, 1f, 0f, 0f)
            }

            ForgeDirection.NORTH -> {
                GL11.glRotatef(-90f, 1f, 0f, 0f)
            }

            ForgeDirection.EAST -> {
                GL11.glRotatef(-90f, 0f, 0f, 1f)
            }

            ForgeDirection.WEST -> {
                GL11.glRotatef(90f, 0f, 0f, 1f)
            }

            else -> {}
        }
    }

    fun getFacingVector(facing: ForgeDirection?): SusVec3? {
        return SusVec3.getVec3FromForgeDirection(facing)
    }

    fun rotateOffsetFromOrientation(offset: Vec3, facing: ForgeDirection?): Vec3? {
        when (facing) {
            ForgeDirection.DOWN -> {
                return offset.xRot(180)
            }

            ForgeDirection.SOUTH -> {
                return offset.xRot(-90)
            }

            ForgeDirection.NORTH -> {
                return offset.xRot(90)
            }

            ForgeDirection.EAST -> {
                return offset.zRot(90)
            }

            ForgeDirection.WEST -> {
                return offset.zRot(-90)
            }

            ForgeDirection.UP -> {
                return offset
            }

            else -> {
                return null
            }
        }
    }

    fun spinOffsetFromOrientationByAngle(offset: Vec3, facing: ForgeDirection?, angle : Int): Vec3? {
        when (facing) {
            ForgeDirection.DOWN -> {
                return offset.yRot(angle)
            }

            ForgeDirection.SOUTH -> {
                return offset.zRot(angle)
            }

            ForgeDirection.NORTH -> {
                return offset.zRot(angle)
            }

            ForgeDirection.EAST -> {
                return offset.xRot(angle)
            }

            ForgeDirection.WEST -> {
                return offset.xRot(angle)
            }

            ForgeDirection.UP -> {
                return offset.yRot(angle)
            }

            else -> {
                return null
            }
        }
    }
}

