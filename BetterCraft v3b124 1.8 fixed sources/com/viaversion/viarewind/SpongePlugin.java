/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  org.spongepowered.api.config.ConfigDir
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.Order
 *  org.spongepowered.api.event.lifecycle.ConstructPluginEvent
 *  org.spongepowered.plugin.builtin.jvm.Plugin
 */
package com.viaversion.viarewind;

import com.google.inject.Inject;
import com.viaversion.viarewind.api.ViaRewindPlatform;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.sponge.util.LoggerWrapper;
import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin(value="viarewind")
public class SpongePlugin
implements ViaRewindPlatform {
    private Logger logger;
    @Inject
    private org.apache.logging.log4j.Logger loggerSlf4j;
    @Inject
    @ConfigDir(sharedRoot=false)
    private Path configDir;

    @Listener(order=Order.LATE)
    public void loadPlugin(ConstructPluginEvent e2) {
        this.logger = new LoggerWrapper(this.loggerSlf4j);
        Via.getManager().addEnableListener(() -> this.init(new File(this.configDir.toFile(), "config.yml")));
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }
}

