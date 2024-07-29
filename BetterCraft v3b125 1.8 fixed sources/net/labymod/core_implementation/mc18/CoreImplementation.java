/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18;

import net.labymod.core.CoreAdapter;
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
import net.labymod.core_implementation.mc18.ForgeImplementation;
import net.labymod.core_implementation.mc18.MappingImplementation;
import net.labymod.core_implementation.mc18.MathImplementation;
import net.labymod.core_implementation.mc18.MinecraftImplementation;
import net.labymod.core_implementation.mc18.ProtocolImplementation;
import net.labymod.core_implementation.mc18.RenderImplementation;
import net.labymod.core_implementation.mc18.RenderPlayerImplementation;
import net.labymod.core_implementation.mc18.ServerPingerImplementation;
import net.labymod.core_implementation.mc18.SoundImplementation;
import net.labymod.core_implementation.mc18.WorldRendererImplementation;

public class CoreImplementation
implements CoreAdapter {
    private ForgeAdapter forgeImpl = new ForgeImplementation();
    private MathAdapter mathImpl = new MathImplementation();
    private MinecraftAdapter minecraftImpl = new MinecraftImplementation();
    private RenderAdapter renderImpl = new RenderImplementation();
    private RenderPlayerAdapter renderPlayerImpl = new RenderPlayerImplementation();
    private SoundAdapter soundImpl = new SoundImplementation();
    private WorldRendererAdapter worldRendererImpl = new WorldRendererImplementation();
    private MappingAdapter mappingImpl = new MappingImplementation();
    private ServerPingerAdapter serverPingerImpl = new ServerPingerImplementation();
    private ProtocolImplementation chunkImpl = new ProtocolImplementation();

    @Override
    public ForgeAdapter getForgeImplementation() {
        return this.forgeImpl;
    }

    @Override
    public MathAdapter getMathImplementation() {
        return this.mathImpl;
    }

    @Override
    public MinecraftAdapter getMinecraftImplementation() {
        return this.minecraftImpl;
    }

    @Override
    public RenderAdapter getRenderImplementation() {
        return this.renderImpl;
    }

    @Override
    public RenderPlayerAdapter getRenderPlayerImplementation() {
        return this.renderPlayerImpl;
    }

    @Override
    public SoundAdapter getSoundImplementation() {
        return this.soundImpl;
    }

    @Override
    public WorldRendererAdapter getWorldRendererImplementation() {
        return this.worldRendererImpl;
    }

    @Override
    public MappingAdapter getMappingAdapter() {
        return this.mappingImpl;
    }

    @Override
    public ServerPingerAdapter getServerPingerImplementation() {
        return this.serverPingerImpl;
    }

    @Override
    public ProtocolAdapter getProtocolAdapter() {
        return this.chunkImpl;
    }
}

