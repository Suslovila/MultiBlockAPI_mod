package com.suslovila.sus_multi_blocked;

import com.suslovila.sus_multi_blocked.api.GuiHandler;
import com.suslovila.sus_multi_blocked.client.ClientProxy;
import com.suslovila.sus_multi_blocked.common.CommonProxy;
import com.suslovila.sus_multi_blocked.common.item.ModItems;
import com.suslovila.sus_multi_blocked.utils.NbtKeyNameHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

@Mod(name = SusMultiBlocked.NAME, modid = SusMultiBlocked.MOD_ID, version = SusMultiBlocked.VERSION)
public class SusMultiBlocked {
	public static final String NAME = "Sus Multi Blocked";
	public static final String MOD_ID = "sus_multi_blocked";
	public static final NbtKeyNameHelper prefixAppender = new NbtKeyNameHelper(MOD_ID);
	public static final String VERSION = "1.0";

	public static final CreativeTabs tab = new CreativeTabs(MOD_ID) {
        @Override
        public Item getTabIconItem() {
            return ModItems.multiBlockFormer;
        }
    };

	@Mod.Instance(MOD_ID)
	public static SusMultiBlocked instance;

	@SidedProxy(clientSide = "com.suslovila.sus_multi_blocked.client.ClientProxy", serverSide = "com.suslovila.sus_multi_blocked.common.CommonProxy")
	public static CommonProxy proxy;


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
		proxy.registerRenderers();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ClientProxy());
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());


	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);

	}
}
