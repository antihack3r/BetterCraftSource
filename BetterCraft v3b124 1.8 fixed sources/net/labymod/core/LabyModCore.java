/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import net.labymod.core.CoreAdapter;
import net.labymod.core.ForgeAdapter;
import net.labymod.core.MappingAdapter;
import net.labymod.core.MathAdapter;
import net.labymod.core.MinecraftAdapter;
import net.labymod.core.RenderAdapter;
import net.labymod.core.RenderPlayerAdapter;
import net.labymod.core.ServerPingerAdapter;
import net.labymod.core.SoundAdapter;
import net.labymod.core.WorldRendererAdapter;

public class LabyModCore {
    private static CoreAdapter coreAdapter;

    public static CoreAdapter getCoreAdapter() {
        return coreAdapter;
    }

    public static void setCoreAdapter(CoreAdapter coreAdapter) {
        LabyModCore.coreAdapter = coreAdapter;
    }

    public static WorldRendererAdapter getWorldRenderer() {
        return coreAdapter.getWorldRendererImplementation();
    }

    public static RenderAdapter getRenderImplementation() {
        return coreAdapter.getRenderImplementation();
    }

    public static RenderPlayerAdapter getRenderPlayerImplementation() {
        return coreAdapter.getRenderPlayerImplementation();
    }

    public static MinecraftAdapter getMinecraft() {
        return coreAdapter.getMinecraftImplementation();
    }

    public static MathAdapter getMath() {
        return coreAdapter.getMathImplementation();
    }

    public static ForgeAdapter getForge() {
        return coreAdapter.getForgeImplementation();
    }

    public static SoundAdapter getSound() {
        return coreAdapter.getSoundImplementation();
    }

    public static MappingAdapter getMappingAdapter() {
        return coreAdapter.getMappingAdapter();
    }

    public static ServerPingerAdapter getServerPinger() {
        return coreAdapter.getServerPingerImplementation();
    }
}

