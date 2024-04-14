package com.suslovila.common.sync;

import com.suslovila.ExampleMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ExampleMod.NAME.toLowerCase());

   public static void init() {
      int idx = 0;

      INSTANCE.registerMessage(PacketMultiBlockFormerModeSwitch.Handler.class, PacketMultiBlockFormerModeSwitch.class, idx++, Side.SERVER);
      INSTANCE.registerMessage(PacketMultiBlockFormerFillSpace.Handler.class, PacketMultiBlockFormerFillSpace.class, idx++, Side.SERVER);
      INSTANCE.registerMessage(PacketMultiBlockFormerWriteToFile.Handler.class, PacketMultiBlockFormerWriteToFile.class, idx++, Side.SERVER);
      INSTANCE.registerMessage(PacketMultiBlockFormerAddModifier.Handler.class, PacketMultiBlockFormerAddModifier.class, idx++, Side.SERVER);
      INSTANCE.registerMessage(PacketSetGlobalModifiers.Handler.class, PacketSetGlobalModifiers.class, idx++, Side.SERVER);
      INSTANCE.registerMessage(PacketBlockModifiers.Handler.class, PacketBlockModifiers.class, idx++, Side.SERVER);


   }
}
