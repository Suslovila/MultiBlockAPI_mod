package com.suslovila.sus_multi_blocked.common.multistructure

import com.suslovila.sus_multi_blocked.api.AdditionalData
import com.suslovila.sus_multi_blocked.api.MultiStructure
import com.suslovila.sus_multi_blocked.api.MultiStructureElement
import com.suslovila.sus_multi_blocked.utils.SusVec3
import com.suslovila.sus_multi_blocked.utils.Vec3
import com.suslovila.sus_multi_blocked.utils.setTile
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import java.nio.file.Paths

object MultiBlockTower : MultiStructure<TowerAdditionalData, TowerElement>(
    Paths.get(".").toAbsolutePath().toString() + "/config/def.json",
    availableFacings = arrayListOf(
        ForgeDirection.DOWN,
        ForgeDirection.SOUTH,
        ForgeDirection.EAST,
    ),
    dataClass = TowerElement::class.java,
    rotatable = true
) {
    val tiles = hashMapOf<String, () -> TileEntity?>(
        "spire" to ::TileEntity,
        "empty" to { null }
    )

    override fun <T : TileEntity> render(tile: T, playersOffset: SusVec3, partialTicks: Float) {

    }

    override fun onCreated(world: World, masterWorldPosition: Vec3, player: EntityPlayerMP?) {

    }
}

class TowerElement(
    x: Int,
    y: Int,
    z: Int,
    storedBlock: String,
    meta: Int,
    val tile: String

) : MultiStructureElement<TowerAdditionalData>(x, y, z, storedBlock, meta) {

    override fun construct(
        world: World,
        masterWorldPosition: Vec3,
        facing: ForgeDirection,
        angle : Int,
        player: EntityPlayerMP?

    ) {
        super.construct(world, masterWorldPosition, facing, angle, player)
        val tile = MultiBlockTower.tiles[tile]?.invoke() ?: return
        world.setTile(getRealPos(masterWorldPosition, facing, angle), tile)
    }

    override fun putAdditionalData() {
        this.additionalData = TowerAdditionalData()
    }
}

class TowerAdditionalData(
    override val fillingBlock: Block = Blocks.brick_block

) : AdditionalData() {
}
