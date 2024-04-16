package com.suslovila.sus_multi_blocked.common.sync;

import com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormer;
import com.suslovila.sus_multi_blocked.common.item.Modifier;
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper;
import com.suslovila.sus_multi_blocked.utils.SusNBTHelper;
import com.suslovila.sus_multi_blocked.utils.Vec3;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;

public class PacketBlockModifiers implements IMessage {
    private ArrayList<Modifier> modifiers;
    private Vec3 pos;
    public PacketBlockModifiers(ArrayList<Modifier> modifiers, Vec3 pos) {
        this.modifiers = modifiers;
        this.pos = pos;
    }

    public PacketBlockModifiers() {

    }

    @Override
    public void toBytes(ByteBuf buf) {
        pos.writeTo(buf);
        buf.writeInt(modifiers.size());
        for (Modifier modifier : modifiers) {
            modifier.writeTo(buf);
        }
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        pos = Vec3.Companion.readFrom(buf);
        ArrayList<Modifier> readModifiers = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            readModifiers.add(Modifier.Companion.readFrom(buf));
        }
        this.modifiers = readModifiers;
    }


    public static class Handler implements IMessageHandler<PacketBlockModifiers, IMessage> {
        @Override
        public IMessage onMessage(PacketBlockModifiers packet, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack stack = player.getHeldItem();
            if (stack == null) return null;

            if (stack.getItem() instanceof ItemMultiBlockFormer) {
                NBTTagCompound tag = SusNBTHelper.INSTANCE.getOrCreateTag(stack);
                NBTTagList listWithBlockInfo = MultiBlockWrapper.INSTANCE.getTagListWithBlocksInfo(stack);
                MultiBlockWrapper.INSTANCE.setBlockInfo(stack, packet.pos, packet.modifiers);
            }
            return null;
        }
    }
}


