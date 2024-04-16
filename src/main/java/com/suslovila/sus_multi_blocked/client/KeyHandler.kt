package com.suslovila.sus_multi_blocked.client

import com.suslovila.sus_multi_blocked.common.sync.PacketHandler
import com.suslovila.sus_multi_blocked.common.sync.PacketMultiBlockFormerFillSpace
import com.suslovila.sus_multi_blocked.common.sync.PacketMultiBlockFormerModeSwitch
import com.suslovila.sus_multi_blocked.common.sync.PacketMultiBlockFormerWriteToFile
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.input.Keyboard


object KeyHandler {
    // Клавиши мода
    private val modeSwitchTrigger = KeyBinding("switch mode", Keyboard.KEY_N, "mcmodding.key.category")
    private val fillEmptySpaceTrigger = KeyBinding("fill empty space", Keyboard.KEY_F, "mcmodding.key.category")
    private val writeToFileTrigger = KeyBinding("write structure to file", Keyboard.KEY_F, "mcmodding.key.category")

    fun register() {
        ClientRegistry.registerKeyBinding(modeSwitchTrigger)
        ClientRegistry.registerKeyBinding(fillEmptySpaceTrigger)
        ClientRegistry.registerKeyBinding(writeToFileTrigger)

        FMLCommonHandler.instance().bus().register(this)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onKeyInput(event: KeyInputEvent?) {
        if (modeSwitchTrigger.isPressed) {
            PacketHandler.INSTANCE.sendToServer(PacketMultiBlockFormerModeSwitch())
        }

        if (fillEmptySpaceTrigger.isPressed) {
            PacketHandler.INSTANCE.sendToServer(PacketMultiBlockFormerFillSpace())
        }
        if (writeToFileTrigger.isPressed) {
            PacketHandler.INSTANCE.sendToServer(PacketMultiBlockFormerWriteToFile())
        }
    }
}