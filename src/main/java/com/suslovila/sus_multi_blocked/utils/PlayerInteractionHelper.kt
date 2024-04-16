package com.suslovila.sus_multi_blocked.utils

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText

object PlayerInteractionHelper {
    fun EntityPlayer.sendChatMessage(msg: String){
        this.addChatMessage(ChatComponentText(msg))

    }
}