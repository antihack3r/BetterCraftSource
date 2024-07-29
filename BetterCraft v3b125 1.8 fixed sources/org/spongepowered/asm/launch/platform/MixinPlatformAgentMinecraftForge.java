/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.Environment
 *  cpw.mods.modlauncher.Launcher
 *  cpw.mods.modlauncher.api.IEnvironment$Keys
 *  cpw.mods.modlauncher.api.ILaunchHandlerService
 *  cpw.mods.modlauncher.api.TypesafeMap$Key
 */
package org.spongepowered.asm.launch.platform;

import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.TypesafeMap;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import org.spongepowered.asm.launch.platform.IMixinPlatformAgent;
import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;

public class MixinPlatformAgentMinecraftForge
extends MixinPlatformAgentAbstract
implements IMixinPlatformServiceAgent {
    private static final String GET_DIST_METHOD = "getDist";

    @Override
    public void init() {
    }

    @Override
    public IMixinPlatformAgent.AcceptResult accept(MixinPlatformManager manager, IContainerHandle handle) {
        return IMixinPlatformAgent.AcceptResult.REJECTED;
    }

    @Override
    public String getSideName() {
        Environment environment = Launcher.INSTANCE.environment();
        String launchTarget = environment.getProperty((TypesafeMap.Key)IEnvironment.Keys.LAUNCHTARGET.get()).orElse("missing").toLowerCase(Locale.ROOT);
        if (launchTarget.contains("server")) {
            return "SERVER";
        }
        if (launchTarget.contains("client")) {
            return "CLIENT";
        }
        Optional launchHandler = environment.findLaunchHandler(launchTarget);
        if (launchHandler.isPresent()) {
            ILaunchHandlerService service = (ILaunchHandlerService)launchHandler.get();
            try {
                Method mdGetDist = service.getClass().getDeclaredMethod(GET_DIST_METHOD, new Class[0]);
                String strDist = mdGetDist.invoke((Object)service, new Object[0]).toString().toLowerCase(Locale.ROOT);
                if (strDist.contains("server")) {
                    return "SERVER";
                }
                if (strDist.contains("client")) {
                    return "CLIENT";
                }
            }
            catch (Exception ex2) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return null;
    }
}

