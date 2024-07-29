/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import net.labymod.core.ForgeAdapter;
import net.labymod.core.MappingAdapter;
import net.labymod.core.MathAdapter;
import net.labymod.core.MinecraftAdapter;
import net.labymod.core.ProtocolAdapter;
import net.labymod.core.RenderAdapter;
import net.labymod.core.RenderPlayerAdapter;
import net.labymod.core.ServerPingerAdapter;
import net.labymod.core.SoundAdapter;
import net.labymod.core.WorldRendererAdapter;

public interface CoreAdapter {
    public ForgeAdapter getForgeImplementation();

    public MathAdapter getMathImplementation();

    public MinecraftAdapter getMinecraftImplementation();

    public RenderAdapter getRenderImplementation();

    public RenderPlayerAdapter getRenderPlayerImplementation();

    public SoundAdapter getSoundImplementation();

    public WorldRendererAdapter getWorldRendererImplementation();

    public MappingAdapter getMappingAdapter();

    public ServerPingerAdapter getServerPingerImplementation();

    public ProtocolAdapter getProtocolAdapter();
}

