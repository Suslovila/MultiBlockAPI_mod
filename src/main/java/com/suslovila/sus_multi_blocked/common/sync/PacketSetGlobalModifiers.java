package com.suslovila.sus_multi_blocked.common.sync;

import com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormer;
import com.suslovila.sus_multi_blocked.common.item.Modifier;
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper;
import com.suslovila.sus_multi_blocked.utils.SusNBTHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

public class PacketSetGlobalModifiers implements IMessage {
    private ArrayList<Modifier> modifiers;
    private String fileName;

    public PacketSetGlobalModifiers(ArrayList<Modifier> modifiers, String fileName) {
        this.modifiers = modifiers;
        this.fileName = fileName;
    }

    public PacketSetGlobalModifiers() {

    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, fileName);
        buf.writeInt(modifiers.size());
        for (Modifier modifier : modifiers) {
            modifier.writeTo(buf);
        }
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        fileName = ByteBufUtils.readUTF8String(buf);
        ArrayList<Modifier> readModifiers = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            readModifiers.add(Modifier.Companion.readFrom(buf));
        }
        this.modifiers = readModifiers;
    }


    public static class Handler implements IMessageHandler<PacketSetGlobalModifiers, IMessage> {
        @Override
        public IMessage onMessage(PacketSetGlobalModifiers packet, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack stack = player.getHeldItem();
            if (stack == null) return null;

            if (stack.getItem() instanceof ItemMultiBlockFormer) {
                NBTTagCompound tag = SusNBTHelper.INSTANCE.getOrCreateTag(stack);
                MultiBlockWrapper.INSTANCE.setModifiers(tag, packet.modifiers);
                MultiBlockWrapper.INSTANCE.setFileName(stack, packet.fileName);
            }
            return null;
        }
    }
}


