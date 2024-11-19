package com.suslovila.sus_multi_blocked.api;

import com.suslovila.sus_multi_blocked.client.gui.GuiMultiBlockFormer;
import com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormer;
import com.suslovila.sus_multi_blocked.utils.Position;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    public static final int GUI_MULTIBLOCK_FORMER = 1;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) {
            return null;
        }

        TileEntity tile = world.getTileEntity(x, y, z);

        switch (id) {
            case GUI_MULTIBLOCK_FORMER: {
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) {
            return null;
        }

        switch (id) {
            case GUI_MULTIBLOCK_FORMER: {
                if (!(player.getHeldItem().getItem() instanceof ItemMultiBlockFormer)) {
                    return null;
                }
                return new GuiMultiBlockFormer(player.inventory, player.getHeldItem(), new Position(x, y, z));
            }
        }
        return null;
    }
}
