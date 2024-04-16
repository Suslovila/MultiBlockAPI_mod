package com.suslovila.sus_multi_blocked.mixin;

import com.suslovila.sus_multi_blocked.mixinUtils.IMixinNbtTagProvider;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = NBTTagCompound.class)
public class MixinNbtTag implements IMixinNbtTagProvider {
    @Shadow
    Map tagMap;

    @Override
    public HashMap tagMap() {
        return (HashMap) tagMap;
    }
}
