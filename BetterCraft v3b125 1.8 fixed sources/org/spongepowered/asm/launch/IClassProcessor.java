/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService$Phase
 */
package org.spongepowered.asm.launch;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.util.EnumSet;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public interface IClassProcessor {
    public EnumSet<ILaunchPluginService.Phase> handlesClass(Type var1, boolean var2, String var3);

    public boolean processClass(ILaunchPluginService.Phase var1, ClassNode var2, Type var3, String var4);

    public boolean generatesClass(Type var1);

    public boolean generateClass(Type var1, ClassNode var2);
}

