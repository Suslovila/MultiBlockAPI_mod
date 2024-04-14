package com.suslovila.api;

import com.suslovila.client.gui.GUIPlayerDetector;
import com.suslovila.common.item.ItemMultiBlockFormer;
import com.suslovila.utils.Vec3;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    public static final int ASSEMBLE_TABLE = 1;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) {
            return null;
        }

        TileEntity tile = world.getTileEntity(x, y, z);

        switch (id) {
            case ASSEMBLE_TABLE: {
//                if (!(tile instanceof TileAssemblyTable)) {
//                    return null;
//                }
//                return new ContainerAssemblyTable(player.inventory, (TileAssemblyTable) tile);

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
            case ASSEMBLE_TABLE: {
                if (!(player.getHeldItem().getItem() instanceof ItemMultiBlockFormer)) {
                    return null;
                }
                return new GUIPlayerDetector(player.inventory, player.getHeldItem(), new Vec3(x, y, z));

            }
        }
        return null;
    }
}
