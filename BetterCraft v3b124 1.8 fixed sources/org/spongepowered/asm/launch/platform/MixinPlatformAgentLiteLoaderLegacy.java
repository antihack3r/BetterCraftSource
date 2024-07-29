/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.launch.platform;

import java.util.Collection;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.launch.platform.IMixinPlatformAgent;
import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;

public class MixinPlatformAgentLiteLoaderLegacy
extends MixinPlatformAgentAbstract
implements IMixinPlatformServiceAgent {
    private static final String GETSIDE_METHOD = "getEnvironmentType";
    private static final String LITELOADER_TWEAKER_NAME = "com.mumfrey.liteloader.launch.LiteLoaderTweaker";

    @Override
    public IMixinPlatformAgent.AcceptResult accept(MixinPlatformManager manager, IContainerHandle handle) {
        return IMixinPlatformAgent.AcceptResult.REJECTED;
    }

    @Override
    public String getSideName() {
        return MixinPlatformAgentAbstract.invokeStringMethod(Launch.classLoader, LITELOADER_TWEAKER_NAME, GETSIDE_METHOD);
    }

    @Override
    public void init() {
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return null;
    }
}

