/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import java.lang.reflect.Field;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.utils.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

public class LavaLightUpdater {
    public static void update() {
        boolean value = LabyMod.getSettings().improvedLavaNoLight;
        Block blockFlowingLava = Block.getBlockById(10);
        Block blockLava = Block.getBlockById(11);
        try {
            LavaLightUpdater.updateLightField(blockLava, value);
            LavaLightUpdater.updateLightField(blockFlowingLava, value);
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        if (LabyMod.getInstance().isInGame()) {
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
        }
    }

    private static void updateLightField(Block block, boolean value) throws Exception {
        Field lightValueField = ReflectionHelper.findField(Block.class, LabyModCore.getMappingAdapter().getLightValueMappings());
        lightValueField.setAccessible(true);
        lightValueField.set(block, value ? 0 : 15);
    }
}

