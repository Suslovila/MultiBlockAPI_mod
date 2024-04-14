package com.suslovila.common.sync;

import com.suslovila.common.item.ItemMultiBlockFormer;
import com.suslovila.common.item.MultiBlockWrapper;
import com.suslovila.utils.*;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;


import static com.suslovila.common.item.ItemMultiBlockFormerKt.*;


public class PacketMultiBlockFormerFillSpace implements IMessage {

    public PacketMultiBlockFormerFillSpace() {
    }

    public void toBytes(ByteBuf buffer) {

    }

    public void fromBytes(ByteBuf buffer) {

    }

    public static class Handler implements IMessageHandler<PacketMultiBlockFormerFillSpace, IMessage> {
        @Override
        public IMessage onMessage(PacketMultiBlockFormerFillSpace packet, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack stack = player.getHeldItem();
            if (stack == null) return null;

            if (stack.getItem() instanceof ItemMultiBlockFormer) {
                Vec3 pos1 = MultiBlockWrapper.INSTANCE.getFirstBound(stack);
                Vec3 pos2 = MultiBlockWrapper.INSTANCE.getSecondBound(stack);
                if (pos1 == null || pos2 == null) {
                    PlayerInteractionHelper.INSTANCE.sendChatMessage(player, "Bounds were not specified correctly");
                } else {
                    fillEmptySpaces(
                            player.worldObj,
                            WorldHelper.INSTANCE.boundingBoxFromTwoPos(pos1, pos2)
                    );
                }
            }

            return null;
        }
    }
}

