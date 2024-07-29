/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.launch;

import java.io.File;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.CommandLineOptions;

public class MixinTweaker
implements ITweaker {
    public MixinTweaker() {
        MixinBootstrap.start();
    }

    @Override
    public final void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        MixinBootstrap.doInit(CommandLineOptions.ofArgs(args));
    }

    @Override
    public final void injectIntoClassLoader(LaunchClassLoader classLoader) {
        MixinBootstrap.inject();
    }

    @Override
    public String getLaunchTarget() {
        return MixinBootstrap.getPlatform().getLaunchTarget();
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}

