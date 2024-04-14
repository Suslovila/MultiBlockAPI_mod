package com.suslovila.common.item;

import com.suslovila.ExampleMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ModItems {

    //public static Item item = new CustomItem();
    public static ItemMultiBlockFormer multiBlockFormer = new ItemMultiBlockFormer();
    public static void register(){
       GameRegistry.registerItem(multiBlockFormer, ExampleMod.MOD_ID + "multiBlockFormer");
    }
}
