package com.suslovila.utils

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ChatComponentText

object PlayerInteractionHelper {
    fun EntityPlayer.sendChatMessage(msg: String){
        this.addChatMessage(ChatComponentText(msg))

    }
}