package com.suslovila.sus_multi_blocked.common.sync;

import com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormer;
import com.suslovila.sus_multi_blocked.common.item.MultiBlockWrapper;
import com.suslovila.sus_multi_blocked.utils.PlayerInteractionHelper;
import com.suslovila.sus_multi_blocked.utils.Position;
import com.suslovila.sus_multi_blocked.utils.WorldHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import static com.suslovila.sus_multi_blocked.common.item.ItemMultiBlockFormerKt.*;

public class PacketMultiBlockFormerWriteToFile implements IMessage {

    public PacketMultiBlockFormerWriteToFile() {
    }

    public void toBytes(ByteBuf buffer) {

    }

    public void fromBytes(ByteBuf buffer) {

    }

    public static class Handler implements IMessageHandler<PacketMultiBlockFormerWriteToFile, IMessage> {
        @Override
        public IMessage onMessage(PacketMultiBlockFormerWriteToFile packet, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack stack = player.getHeldItem();
            if (stack == null) return null;

            if (stack.getItem() instanceof ItemMultiBlockFormer) {
                Position pos1 = MultiBlockWrapper.INSTANCE.getFirstBound(stack);
                Position pos2 = MultiBlockWrapper.INSTANCE.getSecondBound(stack);
                if (pos1 == null || pos2 == null) {
                    PlayerInteractionHelper.INSTANCE.sendChatMessage(player, "Bounds were not specified correctly");
                    return null;
                }
                if (MultiBlockWrapper.INSTANCE.getMasterPos(stack) == null) {
                    PlayerInteractionHelper.INSTANCE.sendChatMessage(player, "Master Position was not set");
                    return null;
                }
                AxisAlignedBB space = WorldHelper.INSTANCE.boundingBoxFromTwoPos(pos1, pos2);
                Position masterPos = MultiBlockWrapper.INSTANCE.getMasterPos(stack);
                net.minecraft.util.Vec3 asCommonVec3 = net.minecraft.util.Vec3.createVectorHelper(masterPos.getX(), masterPos.getY(), masterPos.getZ());
                if(!isVecInside(space, asCommonVec3)) {
                    PlayerInteractionHelper.INSTANCE.sendChatMessage(player, "Master Position is out of selected zone");
                    return null;
                }

                boolean success = writeToJsonFromZoneSelector(stack, player.worldObj);
                if (success) {
                    PlayerInteractionHelper.INSTANCE.sendChatMessage(player, "successfully wrote structure to file!");
                }
            }
            return null;
        }
    }
    public static boolean isVecInside(AxisAlignedBB alignedBB, net.minecraft.util.Vec3 vec)
    {
        return vec.xCoord >= alignedBB.minX && vec.xCoord <= alignedBB.maxX && (vec.yCoord >= alignedBB.minY && vec.yCoord <= alignedBB.maxY && vec.zCoord >= alignedBB.minZ && vec.zCoord <= alignedBB.maxZ);
    }
}

