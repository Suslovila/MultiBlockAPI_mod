//package com.suslovila.sus_multi_blocked.api.multiblock.block
//
//import com.suslovila.sus_multi_blocked.SusMultiBlocked
//import com.suslovila.sus_multi_blocked.utils.Position
//import net.minecraft.nbt.NBTTagCompound
//import net.minecraft.network.NetworkManager
//import net.minecraft.network.Packet
//import net.minecraft.network.play.server.S35PacketUpdateTileEntity
//import net.minecraft.tileentity.TileEntity
//import net.minecraftforge.common.util.ForgeDirection
//import kotlin.properties.Delegates
//
//
//// default implementation of structure tile. If you want, you can create your own (implement ITileMultiStructureElement)
//// recommended: you should separate master tile from other tiles (different classes)
//abstract class TileDefaultMultiStructureElement(
//) : TileEntity(),
//    ITileMultiStructureElement {
//    abstract val packetId: Int
//
//    lateinit var structureMasterPos: Position
//
//    // required to correctly deconstruct multiStructure
//    lateinit var structureFacing: ForgeDirection
//    var structureRotationAngle: Int by Delegates.notNull()
//
//    companion object {
//        val MULTI_STRUCTURE_FACING_NBT = SusMultiBlocked.prefixAppender.doAndGet("structure_facing")
//        val MULTI_STRUCTURE_SPIN_NBT = SusMultiBlocked.prefixAppender.doAndGet("multi_structure_spin")
//
//    }
//
//    final override fun writeToNBT(nbttagcompound: NBTTagCompound) {
//        super.writeToNBT(nbttagcompound)
//        writeCustomNBT(nbttagcompound)
//    }
//
//    open fun writeCustomNBT(nbttagcompound: NBTTagCompound) {
//        structureMasterPos.writeTo(nbttagcompound)
//        nbttagcompound.setInteger(MULTI_STRUCTURE_FACING_NBT, structureFacing.ordinal)
//        nbttagcompound.setInteger(MULTI_STRUCTURE_SPIN_NBT, structureRotationAngle)
//
//    }
//
//    final override fun readFromNBT(nbttagcompound: NBTTagCompound) {
//        super.readFromNBT(nbttagcompound)
//        readCustomNBT(nbttagcompound)
//    }
//
//    open fun readCustomNBT(nbttagcompound: NBTTagCompound) {
//        structureMasterPos = Position.readFrom(nbttagcompound)
//        structureFacing = ForgeDirection.getOrientation(nbttagcompound.getInteger(MULTI_STRUCTURE_FACING_NBT))
//        structureRotationAngle = nbttagcompound.getInteger(MULTI_STRUCTURE_SPIN_NBT)
//    }
//
//
//    override fun getDescriptionPacket(): Packet {
//        val nbttagcompound = NBTTagCompound()
//        writeCustomNBT(nbttagcompound)
//        return S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, packetId, nbttagcompound)
//    }
//
//    override fun onDataPacket(net: NetworkManager?, pkt: S35PacketUpdateTileEntity) {
//        super.onDataPacket(net, pkt)
//        readCustomNBT(pkt.nbtCompound)
//    }
//
//    override fun getMasterPos(): Position = structureMasterPos
//    override fun getFacing(): ForgeDirection = structureFacing
//    override fun getRotationAngle(): Int = structureRotationAngle
//
//    override fun setFacing(facing: ForgeDirection) {
//        this.structureFacing = facing
//    }
//
//    override fun setMasterPos(position: Position) {
//        this.structureMasterPos = position
//    }
//
//    override fun setRotationAngle(angle: Int) {
//        this.structureRotationAngle = angle
//    }
//    open fun markForSaveAndSync() {
//        markForSave()
//        markForSync()
//    }
//
//    open fun markForSave() {
//        worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this)
//    }
//
//    open fun markForSync() {
//        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
//    }
//}