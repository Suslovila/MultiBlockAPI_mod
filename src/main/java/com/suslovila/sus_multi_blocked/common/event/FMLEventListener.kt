package com.suslovila.sus_multi_blocked.common.event

import com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormer
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.getMode
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper.setFirstBound
import com.suslovila.sus_multi_blocked.common.multistructure.MultiBlockTower
import com.suslovila.sus_multi_blocked.utils.PlayerInteractionHelper.sendChatMessage
import com.suslovila.sus_multi_blocked.utils.Position
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.event.entity.player.PlayerInteractEvent

class FMLEventListener {
    //event class
    @SubscribeEvent
    fun onZoneSelectorLeftClick(event: PlayerInteractEvent) {
        with(event) {
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
                val success = MultiBlockTower.tryConstruct(world, Position(x, y, z), entityPlayer as? EntityPlayerMP)
                if (success) entityPlayer.sendChatMessage("successfully created tower")
            }
        }
    }
}

