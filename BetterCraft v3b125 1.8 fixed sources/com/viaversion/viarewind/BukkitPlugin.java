/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.viaversion.viarewind;

import com.viaversion.viarewind.api.ViaRewindPlatform;
import com.viaversion.viaversion.api.Via;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin
extends JavaPlugin
implements ViaRewindPlatform {
    public BukkitPlugin() {
        Via.getManager().addEnableListener(() -> this.init(new File(this.getDataFolder(), "config.yml")));
    }
}

