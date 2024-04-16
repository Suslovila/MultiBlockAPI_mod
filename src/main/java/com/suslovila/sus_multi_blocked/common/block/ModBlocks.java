package com.suslovila.sus_multi_blocked.common.block;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;

public class ModBlocks {
    public static int exampleRenderID = -1;

    public static ExampleBlock block = new ExampleBlock(Material.cake);

    public static void register() {

        GameRegistry.registerBlock(block,"someName");

        //GameRegistry.registerTileEntity(...);

    }

    public static void registerRender(){
        //exampleRenderID = RenderingRegistry.getNextAvailableRenderId();

    }
}
