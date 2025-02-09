/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.Launcher
 */
package org.spongepowered.asm.service.modlauncher;

import cpw.mods.modlauncher.Launcher;
import org.spongepowered.asm.service.IMixinServiceBootstrap;
import org.spongepowered.asm.service.ServiceInitialisationException;

public class MixinServiceModLauncherBootstrap
implements IMixinServiceBootstrap {
    @Override
    public String getName() {
        return "ModLauncher";
    }

    @Override
    public String getServiceClassName() {
        return "org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher";
    }

    @Override
    public void bootstrap() {
        try {
            Launcher.INSTANCE.hashCode();
        }
        catch (Throwable th2) {
            throw new ServiceInitialisationException(this.getName() + " is not available");
        }
    }
}

