package com.suslovila.common.sync;

import com.suslovila.Config;
import com.suslovila.common.item.ItemMultiBlockFormer;
import com.suslovila.common.item.MultiBlockWrapper;
import com.suslovila.utils.PlayerInteractionHelper;
import com.suslovila.utils.SusVec3;
import com.suslovila.utils.Vec3;
import com.suslovila.utils.WorldHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import static com.suslovila.common.item.ItemMultiBlockFormerKt.*;

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
                Vec3 pos1 = MultiBlockWrapper.INSTANCE.getFirstBound(stack);
                Vec3 pos2 = MultiBlockWrapper.INSTANCE.getSecondBound(stack);
                if (pos1 == null || pos2 == null) {
                    PlayerInteractionHelper.INSTANCE.sendChatMessage(player, "Bounds were not specified correctly");
                    return null;
                }
                if (MultiBlockWrapper.INSTANCE.getMasterPos(stack) == null) {
                    PlayerInteractionHelper.INSTANCE.sendChatMessage(player, "Master Position was not set");
                    return null;
                }
                AxisAlignedBB space = WorldHelper.INSTANCE.boundingBoxFromTwoPos(pos1, pos2);
                Vec3 masterPos = MultiBlockWrapper.INSTANCE.getMasterPos(stack);
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

