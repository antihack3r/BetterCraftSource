/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService$Phase
 */
package org.spongepowered.asm.launch;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.util.EnumSet;

public final class Phases {
    public static final EnumSet<ILaunchPluginService.Phase> NONE = EnumSet.noneOf(ILaunchPluginService.Phase.class);
    public static final EnumSet<ILaunchPluginService.Phase> BEFORE_ONLY = EnumSet.of(ILaunchPluginService.Phase.BEFORE);
    public static final EnumSet<ILaunchPluginService.Phase> AFTER_ONLY = EnumSet.of(ILaunchPluginService.Phase.AFTER);

    private Phases() {
    }
}

