/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer;

import com.google.common.base.Strings;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.throwables.CompanionPluginError;
import org.spongepowered.asm.mixin.transformer.MixinConfig;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;

class PluginHandle {
    private static final ILogger logger = MixinService.getService().getLogger("mixin");
    private final MixinConfig parent;
    private final IMixinConfigPlugin plugin;
    private CompatibilityMode mode = CompatibilityMode.NORMAL;
    private Method mdPreApply;
    private Method mdPostApply;

    PluginHandle(MixinConfig parent, IMixinService service, String pluginClassName) {
        IMixinConfigPlugin plugin = null;
        if (!Strings.isNullOrEmpty(pluginClassName)) {
            try {
                Class<?> pluginClass = service.getClassProvider().findClass(pluginClassName, true);
                plugin = (IMixinConfigPlugin)pluginClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable th2) {
                logger.error("Error loading companion plugin class [{}] for mixin config [{}]. The plugin may be out of date: {}:{}", pluginClassName, parent, th2.getClass().getSimpleName(), th2.getMessage(), th2);
                plugin = null;
            }
        }
        this.parent = parent;
        this.plugin = plugin;
    }

    IMixinConfigPlugin get() {
        return this.plugin;
    }

    boolean isAvailable() {
        return this.plugin != null;
    }

    void onLoad(String mixinPackage) {
        if (this.plugin != null) {
            this.plugin.onLoad(mixinPackage);
        }
    }

    String getRefMapperConfig() {
        return this.plugin != null ? this.plugin.getRefMapperConfig() : null;
    }

    List<String> getMixins() {
        return this.plugin != null ? this.plugin.getMixins() : null;
    }

    boolean shouldApplyMixin(String targetName, String className) {
        return this.plugin == null || this.plugin.shouldApplyMixin(targetName, className);
    }

    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, MixinInfo mixinInfo) throws Exception {
        if (this.plugin == null) {
            return;
        }
        if (this.mode == CompatibilityMode.FAILED) {
            throw new IllegalStateException("Companion plugin failure for [" + this.parent + "] plugin [" + this.plugin.getClass() + "]");
        }
        if (this.mode == CompatibilityMode.COMPATIBLE) {
            try {
                this.applyLegacy(this.mdPreApply, targetClassName, targetClass, mixinClassName, mixinInfo);
            }
            catch (Exception ex2) {
                this.mode = CompatibilityMode.FAILED;
                throw ex2;
            }
            return;
        }
        try {
            this.plugin.preApply(targetClassName, targetClass, mixinClassName, mixinInfo);
        }
        catch (AbstractMethodError ex3) {
            this.mode = CompatibilityMode.COMPATIBLE;
            this.initReflection();
            this.preApply(targetClassName, targetClass, mixinClassName, mixinInfo);
        }
    }

    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, MixinInfo mixinInfo) throws Exception {
        if (this.plugin == null) {
            return;
        }
        if (this.mode == CompatibilityMode.FAILED) {
            throw new IllegalStateException("Companion plugin failure for [" + this.parent + "] plugin [" + this.plugin.getClass() + "]");
        }
        if (this.mode == CompatibilityMode.COMPATIBLE) {
            try {
                this.applyLegacy(this.mdPostApply, targetClassName, targetClass, mixinClassName, mixinInfo);
            }
            catch (Exception ex2) {
                this.mode = CompatibilityMode.FAILED;
                throw ex2;
            }
            return;
        }
        try {
            this.plugin.postApply(targetClassName, targetClass, mixinClassName, mixinInfo);
        }
        catch (AbstractMethodError ex3) {
            this.mode = CompatibilityMode.COMPATIBLE;
            this.initReflection();
            this.postApply(targetClassName, targetClass, mixinClassName, mixinInfo);
        }
    }

    private void initReflection() {
        if (this.mdPreApply != null) {
            return;
        }
        try {
            Class<?> pluginClass = this.plugin.getClass();
            this.mdPreApply = pluginClass.getMethod("preApply", String.class, ClassNode.class, String.class, IMixinInfo.class);
            this.mdPostApply = pluginClass.getMethod("postApply", String.class, ClassNode.class, String.class, IMixinInfo.class);
        }
        catch (Throwable th2) {
            logger.catching(th2);
        }
    }

    private void applyLegacy(Method method, String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        try {
            method.invoke((Object)this.plugin, targetClassName, new ClassNode(targetClass), mixinClassName, mixinInfo);
        }
        catch (LinkageError err) {
            throw new CompanionPluginError(this.apiError("Accessing [" + err.getMessage() + "]"), err);
        }
        catch (IllegalAccessException ex2) {
            throw new CompanionPluginError(this.apiError("Fallback failed [" + ex2.getMessage() + "]"), ex2);
        }
        catch (IllegalArgumentException ex3) {
            throw new CompanionPluginError(this.apiError("Fallback failed [" + ex3.getMessage() + "]"), ex3);
        }
        catch (InvocationTargetException ex4) {
            Throwable th2 = ex4.getCause() != null ? ex4.getCause() : ex4;
            throw new CompanionPluginError(this.apiError("Fallback failed [" + th2.getMessage() + "]"), th2);
        }
    }

    private String apiError(String message) {
        return String.format("Companion plugin attempted to use a deprected API in [%s] plugin [%s]: %s", this.parent, this.plugin.getClass().getName(), message);
    }

    static enum CompatibilityMode {
        NORMAL,
        COMPATIBLE,
        FAILED;

    }
}

