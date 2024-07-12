package com.suslovila.sus_multi_blocked.api.multiblock

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.suslovila.sus_multi_blocked.api.SusTypeToken
import com.suslovila.sus_multi_blocked.api.fromJsons
import com.suslovila.sus_multi_blocked.api.multiblock.block.ITileMultiStructureElement
import com.suslovila.sus_multi_blocked.utils.*
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import java.io.IOException
import java.io.InputStreamReader

//by default all structures in files MUST BE IN "UP" POSITION!!!!
//when clicking on block, it tries to predict it's master's position and fires a check from possible positions
//by default all offsets are made for UP sustain! The "real offset" represents an offset for current facing!

abstract class MultiStructure<D : AdditionalData, E : MultiStructureElement<D>>(
    val sourcePath: String,
    val dataClass: Class<E>,
    val availableFacings: List<ForgeDirection> = arrayListOf(ForgeDirection.UP),
    val rotatable: Boolean = false,
    val validationType: VALIDATION_TYPE = VALIDATION_TYPE.MASTER
) {

    val rotationAngles: ArrayList<Int>
        get() {
            return if (rotatable) arrayListOf(0, 90, 180, 270) else arrayListOf(0)
        }


    val reader = JsonReader(InputStreamReader(dataClass.getResourceAsStream(sourcePath)!!))

    val elements: ArrayList<E> = run {
        val newType = SusTypeToken.getParameterized(List::class.java, dataClass)
        (Gson().fromJsons(reader, newType) as? ArrayList<E>)
            ?.onEach { it.putAdditionalData() }
            ?: throw IOException("error reading structure: $sourcePath")
    }


    open fun tryConstruct(world: World, clickedPosition: Position, player: EntityPlayer?): Boolean {
        val block = world.getBlock(clickedPosition) ?: return false
        if (validationType == VALIDATION_TYPE.EACH_BLOCK) {
            //to provide generating by clicking any block, we filter only available by block type
            val suitableElements = elements.filter {
                Block.getBlockFromName(it.storedBlock) == block &&
                        world.getBlockMetadata(clickedPosition) == it.meta
            }
            for (element in suitableElements) {
                for (face in availableFacings) {
                    for (angle in rotationAngles) {
                        val successConstructing =
                            canConstruct(
                                world,
                                masterPosition = clickedPosition - element.getRealOffset(face, angle),
                                face,
                                angle
                            )
                        if (successConstructing) {
                            finaliseConstruction(
                                world,
                                clickedPosition - element.getRealOffset(face, angle),
                                face,
                                angle,
                                player
                            )
                            return true
                        }
                    }
                }
            }
        } else {
            for (face in availableFacings) {
                for (angle in rotationAngles) {
                    val successConstructing =
                        canConstruct(
                            world,
                            masterPosition = clickedPosition,
                            face,
                            angle
                        )
                    if (successConstructing) {
                        finaliseConstruction(
                            world,
                            clickedPosition,
                            face,
                            angle,
                            player
                        )
                        return true
                    }
                }
            }
        }
        return false
    }

    open fun canConstruct(world: World, clickedPosition: Position, player: EntityPlayer?): Boolean {
        val block = world.getBlock(clickedPosition) ?: return false
        if (validationType == VALIDATION_TYPE.EACH_BLOCK) {
            //to provide generating by clicking any block, we filter only available by block type
            val suitableElements = elements.filter {
                Block.getBlockFromName(it.storedBlock) == block &&
                        world.getBlockMetadata(clickedPosition) == it.meta
            }
            for (element in suitableElements) {
                for (face in availableFacings) {
                    for (angle in rotationAngles) {
                        val successConstructing =
                            canConstruct(
                                world,
                                masterPosition = clickedPosition - element.getRealOffset(face, angle),
                                face,
                                angle
                            )
                        if (successConstructing) {
                            return true
                        }
                    }
                }
            }
        } else {
            for (face in availableFacings) {
                for (angle in rotationAngles) {
                    val successConstructing =
                        canConstruct(
                            world,
                            masterPosition = clickedPosition,
                            face,
                            angle
                        )
                    if (successConstructing) {
                        return true
                    }
                }
            }
        }
        return false
    }


    private fun canConstruct(
        world: World,
        masterPosition: Position,
        facing: ForgeDirection,
        rotationAngle: Int
    ): Boolean =
        elements.all { it.canConstruct(world, masterWorldPosition = masterPosition, facing, rotationAngle) }

    open fun finaliseConstruction(
        world: World,
        masterPosition: Position,
        facing: ForgeDirection,
        rotationAngle: Int,
        player: EntityPlayer?
    ) {
        elements.forEachIndexed {index, element -> element.construct(world, masterPosition, facing, rotationAngle, index, player) }
        onCreated(world, masterPosition, facing, rotationAngle, player)
    }

    open fun isStillValid(world: World, masterPosition: Position, facing: ForgeDirection, angle: Int): Boolean =
        elements.all { it.isStillValid(world, masterPosition, facing, angle) }


    open fun deconstruct(world: World, masterPosition: Position, facing: ForgeDirection, angle: Int) {
        elements.forEach { it.deconstruct(world, masterPosition, facing, angle) }
    }

    abstract fun <T : TileEntity> render(tile: T, playersOffset: SusVec3, partialTicks: Float)
    open fun onCreated(
        world: World,
        masterWorldPosition: Position,
        facing: ForgeDirection,
        angle: Int,
        player: EntityPlayer?
    ) {
        elements.map { it.getRealPos(masterWorldPosition, facing, angle) }.forEach {
            world.markBlockForUpdate(it.x, it.y, it.z)

        }
    }
}


abstract class MultiStructureElement<D : AdditionalData>(
    val x: Int,
    val y: Int,
    val z: Int,
    val storedBlock: String,
    val meta: Int
) {
    lateinit var additionalData: D

    val offset: Position
        get() = Position(x, y, z)

    fun getRealOffset(facing: ForgeDirection, angle: Int): Position {
        val rotated = RotationHelper.rotateOffsetFromOrientation(this.offset, facing)
            ?: throw Exception("Error rotating offset: ${this.offset}")
        val rotatedAndSpinned = RotationHelper.spinOffsetFromOrientationByAngle(rotated, facing, angle)
            ?: throw Exception("Error spinning offset: ${this.offset} by angle: $angle with facing: $facing")
        return rotatedAndSpinned
    }

    open fun canConstruct(
        world: World,
        masterWorldPosition: Position,
        facing: ForgeDirection,
        rotationAngle: Int
    ): Boolean {
        val realPos = getRealPos(masterWorldPosition, facing, rotationAngle)
        val checkBlock = world.getBlock(realPos)
        if (checkBlock != null) {
            return (checkBlock == Block.getBlockFromName(storedBlock) && world.getBlockMetadata(realPos) == meta)
        }
        return false
    }

    open fun construct(
        world: World,
        masterWorldPosition: Position,
        facing: ForgeDirection,
        angle: Int,
        index: Int,
        player: EntityPlayer?,
    ) {
        val realPos = masterWorldPosition + getRealOffset(facing, angle)
        world.setBlock(realPos, additionalData.fillingBlock)
        val tile = (world.getTile(realPos) as? ITileMultiStructureElement) ?: return
        tile.setMasterPos(masterWorldPosition)
        tile.setFacing(facing)
        tile.setRotationAngle(angle)
        // required for drops
        tile.setElementIndex(index)
    }

    open fun isStillValid(
        world: World,
        masterWorldPosition: Position,
        facing: ForgeDirection,
        angle: Int
    ): Boolean {
        val realPos = getRealPos(masterWorldPosition, facing, angle)
        val block = world.getBlock(realPos)
        if (block != null) {
            return block == additionalData.fillingBlock
        }
        return false
    }

    open fun deconstruct(
        world: World,
        masterWorldPosition: Position,
        facing: ForgeDirection,
        angle: Int
    ) {
        val realPos = masterWorldPosition + getRealOffset(facing, angle)
        val block = world.getBlock(realPos)
        if (block == null || block != additionalData.fillingBlock) return

        world.setBlock(realPos.x, realPos.y, realPos.z, Block.getBlockFromName(storedBlock), meta, 2)
    }

    fun getRealPos(masterPosition: Position, facing: ForgeDirection, angle: Int) =
        masterPosition + getRealOffset(facing, angle)

    abstract fun putAdditionalData()

}

abstract class AdditionalData {
    abstract val fillingBlock: Block
}

enum class VALIDATION_TYPE {
    MASTER,
    EACH_BLOCK
}