/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.serviceapi.ILaunchPluginService$Phase
 */
package org.spongepowered.asm.service.modlauncher;

import com.google.common.base.Preconditions;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.util.EnumSet;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.IClassProcessor;
import org.spongepowered.asm.launch.Phases;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.ISyntheticClassRegistry;

public class MixinTransformationHandler
implements IClassProcessor {
    private IMixinTransformerFactory transformerFactory;
    private IMixinTransformer transformer;
    private ISyntheticClassRegistry registry;

    void offer(IMixinTransformerFactory transformerFactory) {
        Preconditions.checkNotNull(transformerFactory, "transformerFactory");
        this.transformerFactory = transformerFactory;
    }

    @Override
    public EnumSet<ILaunchPluginService.Phase> handlesClass(Type classType, boolean isEmpty, String reason) {
        if (!isEmpty) {
            return Phases.AFTER_ONLY;
        }
        if (this.registry == null) {
            return null;
        }
        return this.generatesClass(classType) ? Phases.AFTER_ONLY : null;
    }

    @Override
    public boolean generatesClass(Type classType) {
        return this.registry.findSyntheticClass(classType.getClassName()) != null;
    }

    @Override
    public synchronized boolean processClass(ILaunchPluginService.Phase phase, ClassNode classNode, Type classType, String reason) {
        if (phase == ILaunchPluginService.Phase.BEFORE) {
            return false;
        }
        if (this.transformer == null) {
            if (this.transformerFactory == null) {
                throw new IllegalStateException("processClass called before transformer factory offered to transformation handler");
            }
            this.transformer = this.transformerFactory.createTransformer();
            this.registry = this.transformer.getExtensions().getSyntheticClassRegistry();
        }
        if ("mixin".equals(reason)) {
            return false;
        }
        if (this.generatesClass(classType)) {
            return this.generateClass(classType, classNode);
        }
        MixinEnvironment environment = MixinEnvironment.getCurrentEnvironment();
        if ("computing_frames".equals(reason)) {
            return this.transformer.computeFramesForClass(environment, classType.getClassName(), classNode);
        }
        return this.transformer.transformClass(environment, classType.getClassName(), classNode);
    }

    @Override
    public boolean generateClass(Type classType, ClassNode classNode) {
        return this.transformer.generateClass(MixinEnvironment.getCurrentEnvironment(), classType.getClassName(), classNode);
    }
}

