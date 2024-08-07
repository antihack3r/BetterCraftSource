/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.Config;
import org.spongepowered.asm.service.MixinService;

public final class Mixins {
    private static final ILogger logger = MixinService.getService().getLogger("mixin");
    private static final GlobalProperties.Keys CONFIGS_KEY = GlobalProperties.Keys.of(GlobalProperties.Keys.CONFIGS + ".queue");
    private static final Set<String> errorHandlers = new LinkedHashSet<String>();
    private static final Set<String> registeredConfigs = new HashSet<String>();

    private Mixins() {
    }

    public static void addConfigurations(String ... configFiles) {
        MixinEnvironment fallback = MixinEnvironment.getDefaultEnvironment();
        for (String configFile : configFiles) {
            Mixins.createConfiguration(configFile, fallback);
        }
    }

    public static void addConfiguration(String configFile) {
        Mixins.createConfiguration(configFile, MixinEnvironment.getDefaultEnvironment());
    }

    @Deprecated
    static void addConfiguration(String configFile, MixinEnvironment fallback) {
        Mixins.createConfiguration(configFile, fallback);
    }

    private static void createConfiguration(String configFile, MixinEnvironment fallback) {
        Config config = null;
        try {
            config = Config.create(configFile, fallback);
        }
        catch (Exception ex2) {
            logger.error("Error encountered reading mixin config " + configFile + ": " + ex2.getClass().getName() + " " + ex2.getMessage(), ex2);
        }
        Mixins.registerConfiguration(config);
    }

    private static void registerConfiguration(Config config) {
        if (config == null || registeredConfigs.contains(config.getName())) {
            return;
        }
        MixinEnvironment env = config.getEnvironment();
        if (env != null) {
            env.registerConfig(config.getName());
        }
        Mixins.getConfigs().add(config);
        registeredConfigs.add(config.getName());
        Config parent = config.getParent();
        if (parent != null) {
            Mixins.registerConfiguration(parent);
        }
    }

    public static int getUnvisitedCount() {
        int count = 0;
        for (Config config : Mixins.getConfigs()) {
            if (config.isVisited()) continue;
            ++count;
        }
        return count;
    }

    public static Set<Config> getConfigs() {
        LinkedHashSet mixinConfigs = (LinkedHashSet)GlobalProperties.get(CONFIGS_KEY);
        if (mixinConfigs == null) {
            mixinConfigs = new LinkedHashSet();
            GlobalProperties.put(CONFIGS_KEY, mixinConfigs);
        }
        return mixinConfigs;
    }

    public static Set<IMixinInfo> getMixinsForClass(String className) {
        ClassInfo classInfo = ClassInfo.fromCache(className);
        if (classInfo != null) {
            return classInfo.getAppliedMixins();
        }
        return Collections.emptySet();
    }

    public static void registerErrorHandlerClass(String handlerName) {
        if (handlerName != null) {
            errorHandlers.add(handlerName);
        }
    }

    public static Set<String> getErrorHandlerClasses() {
        return Collections.unmodifiableSet(errorHandlers);
    }
}

