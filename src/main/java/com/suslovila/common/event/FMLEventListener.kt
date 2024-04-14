package com.suslovila.common.event

import com.suslovila.common.item.ItemMultiBlockFormer
import com.suslovila.common.item.MultiBlockWrapper
import com.suslovila.common.item.MultiBlockWrapper.getMode
import com.suslovila.common.item.MultiBlockWrapper.setFirstBound
import com.suslovila.common.multistructure.MultiBlockTower
import com.suslovila.utils.PlayerInteractionHelper.sendChatMessage
import com.suslovila.utils.Vec3
import com.suslovila.utils.getBlock
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.event.entity.player.PlayerInteractEvent

class FMLEventListener {
    //event class
    @SubscribeEvent
    fun onZoneSelectorLeftClick(event: PlayerInteractEvent) {
        with(event) {
            println(MultiBlockTower.elements[0])
            val heldItemStack = entityPlayer?.heldItem ?: return
            val isLeftClickWithZoneSelector =
                action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK &&
                        heldItemStack.item is ItemMultiBlockFormer &&
                        heldItemStack.getMode() == MultiBlockWrapper.MODE.ZONE_SELECTOR

            if (isLeftClickWithZoneSelector) {
                heldItemStack.setFirstBound(event.x, event.y, event.z)
                event.isCanceled = true
            }
        }
    }

    @SubscribeEvent
    fun construct(event: PlayerInteractEvent) {
        with(event) {
            if (action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && !world.isRemote) {
                val block = world.getBlock(x, y, z)
                val success = MultiBlockTower.tryConstruct(world, Vec3(x, y, z), entityPlayer as? EntityPlayerMP)
                if (success) entityPlayer.sendChatMessage("successfully created tower")
            }
        }
    }
}

