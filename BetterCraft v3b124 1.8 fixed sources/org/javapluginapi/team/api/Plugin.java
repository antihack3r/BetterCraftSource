/*
 * Decompiled with CFR 0.152.
 */
package org.javapluginapi.team.api;

import org.javapluginapi.team.api.PluginDescription;

public interface Plugin {
    public static final ThreadLocal<PluginDescription> descriptionFile = new ThreadLocal();

    public void onEnable();

    public void onDisable();

    default public void setDescriptionFile(PluginDescription descriptionFile) {
        if (Plugin.descriptionFile.get() != null) {
            return;
        }
        Plugin.descriptionFile.set(descriptionFile);
    }

    default public PluginDescription getDescriptionFile() {
        return descriptionFile.get();
    }
}

