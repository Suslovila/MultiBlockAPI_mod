package com.suslovila.sus_multi_blocked.common


import com.suslovila.sus_multi_blocked.Config
import com.suslovila.sus_multi_blocked.common.block.ModBlocks
import com.suslovila.sus_multi_blocked.common.event.FMLEventListener
import com.suslovila.sus_multi_blocked.common.event.SweetMixinListener
import com.suslovila.sus_multi_blocked.common.item.ModItems
import com.suslovila.sus_multi_blocked.common.sync.PacketHandler
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
