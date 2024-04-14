package com.suslovila.common


import com.suslovila.Config
import com.suslovila.common.block.ModBlocks
import com.suslovila.common.event.FMLEventListener
import com.suslovila.common.event.SweetMixinListener
import com.suslovila.common.item.ModItems
import com.suslovila.common.sync.PacketHandler
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.common.MinecraftForge


open class CommonProxy {
    open fun preInit(event : FMLPreInitializationEvent) {
		Config.registerServerConfig(event.suggestedConfigurationFile)
        FMLCommonHandler.instance().bus().register(FMLEventListener())
        MinecraftForge.EVENT_BUS.register(FMLEventListener())
        MinecraftForge.EVENT_BUS.register(SweetMixinListener())
        ModBlocks.register()
        ModItems.register()
        PacketHandler.init()
    }


    open fun init(event : FMLInitializationEvent) {

    }


    open fun postInit(event : FMLPostInitializationEvent) {

    }

    open fun registerRenderers() {}


}
