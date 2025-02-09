/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.api.NamedPath
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService$ITransformerLoader
 */
package org.spongepowered.asm.launch;

import cpw.mods.modlauncher.api.NamedPath;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.spongepowered.asm.launch.MixinLaunchPluginLegacy;

public class MixinLaunchPlugin
extends MixinLaunchPluginLegacy {
    public void initializeLaunch(ILaunchPluginService.ITransformerLoader transformerLoader, NamedPath[] specialPaths) {
        this.initializeLaunch(transformerLoader);
    }
}

