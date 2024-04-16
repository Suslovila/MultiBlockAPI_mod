package com.suslovila.sus_multi_blocked.common.sync;

import com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormer;
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper;
import com.suslovila.sus_multi_blocked.utils.PlayerInteractionHelper;
import com.suslovila.sus_multi_blocked.utils.SusNBTHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public class PacketMultiBlockFormerModeSwitch implements IMessage {

    public PacketMultiBlockFormerModeSwitch() {
    }

    public void toBytes(ByteBuf buffer) {

    }

    public void fromBytes(ByteBuf buffer) {

    }

    public static class Handler implements IMessageHandler<PacketMultiBlockFormerModeSwitch, IMessage> {
        @Override
        public IMessage onMessage(PacketMultiBlockFormerModeSwitch packet, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack stack = player.getHeldItem();
            if (stack == null) return null;

            if (stack.getItem() instanceof ItemMultiBlockFormer) {
                NBTTagCompound tag = SusNBTHelper.INSTANCE.getOrCreateTag(stack);
                String tagName = MultiBlockWrapper.INSTANCE.getMODE_NAME();
                if (tag.hasKey(tagName)) {
                    tag.setInteger(tagName, (tag.getInteger(tagName) + 1) % MultiBlockWrapper.MODE.values().length);
                    PlayerInteractionHelper.INSTANCE.sendChatMessage(player, "Current Mode: " + MultiBlockWrapper.INSTANCE.getMode(stack));
                }
            }

            return null;
        }
    }
}

