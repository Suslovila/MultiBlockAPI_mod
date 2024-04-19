package com.suslovila.sus_multi_blocked.api.multiblock.block

import com.suslovila.sus_multi_blocked.SusMultiBlocked
import com.suslovila.sus_multi_blocked.utils.Position
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection


// default implementation of structure tile. If you want, you can create your own (implement ITileMultiStructureElement)
// recommended: you should separate master tile from other tiles (different classes)
abstract class TileDefaultMultiStructureElement(
    private var masterPos: Position,

    // required to correctly deconstruct multiStructure
    private var facing: ForgeDirection,
    private var rotationAngle: Int
) : TileEntity(),
    ITileMultiStructureElement {
    abstract val packetId: Int
    companion object {
        val MULTI_STRUCTURE_FACING_NBT = SusMultiBlocked.prefixAppender.doAndGet("structure_facing")
        val MULTI_STRUCTURE_SPIN_NBT = SusMultiBlocked.prefixAppender.doAndGet("multi_structure_spin")

    }
    override fun writeToNBT(nbttagcompound: NBTTagCompound) {
        super.writeToNBT(nbttagcompound)
        writeCustomNBT(nbttagcompound)
    }

    fun writeCustomNBT(nbttagcompound: NBTTagCompound) {
        masterPos.writeTo(nbttagcompound);
        nbttagcompound.setInteger(MULTI_STRUCTURE_FACING_NBT, facing.ordinal)
        nbttagcompound.setInteger(MULTI_STRUCTURE_SPIN_NBT, rotationAngle)

    }

    override fun readFromNBT(nbttagcompound: NBTTagCompound) {
        super.readFromNBT(nbttagcompound)
        readCustomNBT(nbttagcompound)
    }

    fun readCustomNBT(nbttagcompound: NBTTagCompound) {
        masterPos = Position.readFrom(nbttagcompound);
        facing = ForgeDirection.getOrientation(nbttagcompound.getInteger(MULTI_STRUCTURE_FACING_NBT))
        rotationAngle = nbttagcompound.getInteger(MULTI_STRUCTURE_SPIN_NBT)
    }


    override fun getDescriptionPacket(): Packet {
        val nbttagcompound = NBTTagCompound()
        writeCustomNBT(nbttagcompound)
        return S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, packetId, nbttagcompound)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: S35PacketUpdateTileEntity) {
        super.onDataPacket(net, pkt)
        readCustomNBT(pkt.nbtCompound)
    }

    override fun getMasterPos(): Position = masterPos
    override fun getFacing(): ForgeDirection = facing
    override fun getRotationAngle(): Int = rotationAngle
}