/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.servermanager;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
    private Map<String, JsonObject> serverConfigs = new HashMap<String, JsonObject>();

    public Map<String, JsonObject> getServerConfigs() {
        return this.serverConfigs;
    }
}

