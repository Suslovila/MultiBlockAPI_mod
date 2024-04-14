package com.suslovila.client

import com.suslovila.common.sync.PacketHandler
import com.suslovila.common.sync.PacketMultiBlockFormerFillSpace
import com.suslovila.common.sync.PacketMultiBlockFormerModeSwitch
import com.suslovila.common.sync.PacketMultiBlockFormerWriteToFile
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
    private val fillEmptySpaceTrigger = KeyBinding("fill empty space", Keyboard.KEY_F, "mcmodding.key.category2")
    private val writeToFileTrigger = KeyBinding("write structure to file", Keyboard.KEY_F, "mcmodding.key.category2")

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