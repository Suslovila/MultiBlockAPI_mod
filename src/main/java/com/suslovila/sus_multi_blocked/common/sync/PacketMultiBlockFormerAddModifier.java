package com.suslovila.sus_multi_blocked.common.sync;

import com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormer;
import com.suslovila.sus_multi_blocked.utils.SusNBTHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PacketMultiBlockFormerAddModifier implements IMessage {
    private String name;
    private String value;
    public PacketMultiBlockFormerAddModifier(String name, String value){
    this.name = name;
    this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        name = ByteBufUtils.readUTF8String(buf);
        value = ByteBufUtils.readUTF8String(buf);

    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, value);
    }

    public static class Handler implements IMessageHandler<PacketMultiBlockFormerAddModifier, IMessage> {
        @Override
        public IMessage onMessage(PacketMultiBlockFormerAddModifier packet, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack stack = player.getHeldItem();
            if (stack == null) return null;

            if (stack.getItem() instanceof ItemMultiBlockFormer) {
                NBTTagCompound tag = SusNBTHelper.INSTANCE.getOrCreateTag(stack);

//                if(!tag.hasKey(MultiBlockWrapper.INSTANCE.getMODIFIERS_NAME())){
//                    tag.setTag(ItemMultiBlockFormer.Companion.getMODIFIERS_TAG_NAME(), new NBTTagList());
//                }
//                NBTTagList modifiers = tag.getTagList(ItemMultiBlockFormer.Companion.getMODIFIERS_TAG_NAME(), SusNBTHelper.TAG_COMPOUND);
//                NBTTagCompound newModifierTag = new NBTTagCompound();
//                newModifierTag.setString(ItemMultiBlockFormer.Companion.getSINGLE_MODIFIER_TAG_NAME(), packet.name);
//                newModifierTag.setString(ItemMultiBlockFormer.Companion.getSINGLE_MODIFIER_TAG_VALUE(), packet.value);
//                modifiers.appendTag(newModifierTag);
            }
            return null;
        }
    }
}


