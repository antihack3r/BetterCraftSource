/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.service.mojang;

import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.service.IMixinServiceBootstrap;
import org.spongepowered.asm.service.ServiceInitialisationException;

public class MixinServiceLaunchWrapperBootstrap
implements IMixinServiceBootstrap {
    private static final String SERVICE_PACKAGE = "org.spongepowered.asm.service.";
    private static final String LAUNCH_PACKAGE = "org.spongepowered.asm.launch.";
    private static final String LOGGING_PACKAGE = "org.spongepowered.asm.logging.";
    private static final String MIXIN_UTIL_PACKAGE = "org.spongepowered.asm.util.";
    private static final String LEGACY_ASM_PACKAGE = "org.spongepowered.asm.lib.";
    private static final String ASM_PACKAGE = "org.objectweb.asm.";
    private static final String MIXIN_PACKAGE = "org.spongepowered.asm.mixin.";

    @Override
    public String getName() {
        return "LaunchWrapper";
    }

    @Override
    public String getServiceClassName() {
        return "org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper";
    }

    @Override
    public void bootstrap() {
        try {
            Launch.classLoader.hashCode();
        }
        catch (Throwable th2) {
            throw new ServiceInitialisationException(this.getName() + " is not available");
        }
        Launch.classLoader.addClassLoaderExclusion(SERVICE_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(LAUNCH_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(LOGGING_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(ASM_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(LEGACY_ASM_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(MIXIN_PACKAGE);
        Launch.classLoader.addClassLoaderExclusion(MIXIN_UTIL_PACKAGE);
    }
}

