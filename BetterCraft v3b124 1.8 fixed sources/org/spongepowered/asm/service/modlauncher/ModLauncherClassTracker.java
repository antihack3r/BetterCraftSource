/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService$Phase
 */
package org.spongepowered.asm.service.modlauncher;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.IClassProcessor;
import org.spongepowered.asm.launch.Phases;
import org.spongepowered.asm.service.IClassTracker;

public class ModLauncherClassTracker
implements IClassProcessor,
IClassTracker {
    private final Set<String> invalidClasses = new HashSet<String>();
    private final Set<String> loadedClasses = new HashSet<String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerInvalidClass(String className) {
        Set<String> set = this.invalidClasses;
        synchronized (set) {
            this.invalidClasses.add(className);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isClassLoaded(String className) {
        Set<String> set = this.loadedClasses;
        synchronized (set) {
            return this.loadedClasses.contains(className);
        }
    }

    @Override
    public String getClassRestrictions(String className) {
        return "";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EnumSet<ILaunchPluginService.Phase> handlesClass(Type classType, boolean isEmpty, String reason) {
        String name = classType.getClassName();
        Set<String> set = this.invalidClasses;
        synchronized (set) {
            if (this.invalidClasses.contains(name)) {
                throw new NoClassDefFoundError(String.format("%s is invalid", name));
            }
        }
        return Phases.AFTER_ONLY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, String reason) {
        if ("classloading".equals(reason)) {
            Set<String> set = this.loadedClasses;
            synchronized (set) {
                this.loadedClasses.add(classType.getClassName());
            }
        }
        return false;
    }

    @Override
    public boolean generatesClass(Type classType) {
        return false;
    }

    @Override
    public boolean generateClass(Type classType, ClassNode classNode) {
        return false;
    }
}

