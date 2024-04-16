package com.suslovila.sus_multi_blocked.client


import com.suslovila.sus_multi_blocked.client.render.ClientEventHandler


import com.suslovila.sus_multi_blocked.common.CommonProxy
import com.suslovila.sus_multi_blocked.common.block.ModBlocks
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge


class ClientProxy : CommonProxy(), IGuiHandler {
    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
        ModBlocks.registerRender()
        setupItemRenderers()
        val keyHandler = KeyHandler
        keyHandler.register()

        //ClientRegistry.bindTileEntitySpecialRenderer(TileClass::class.java, TileRendererInstance)


       // RenderingRegistry.registerBlockHandler(BlockRenderer())

    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)
    }

    override fun registerRenderers() {
        MinecraftForge.EVENT_BUS.register(ClientEventHandler())
        //MinecraftForgeClient.registerItemRenderer(ModItems.item, CustomItemRenderer)
    }

    private fun setupItemRenderers() {
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), ItemRenderer())
    }

    override fun getServerGuiElement(guiId: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? { return null }

    override fun getClientGuiElement(guiId: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? { return null }

}