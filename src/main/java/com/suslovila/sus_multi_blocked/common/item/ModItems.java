package com.suslovila.sus_multi_blocked.common.item;

import com.suslovila.sus_multi_blocked.SusMultiBlocked;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {

    //public static Item item = new CustomItem();
    public static ItemMultiBlockFormer multiBlockFormer = new ItemMultiBlockFormer();
    public static void register(){
       GameRegistry.registerItem(multiBlockFormer, SusMultiBlocked.MOD_ID + "multiBlockFormer");
    }
}
