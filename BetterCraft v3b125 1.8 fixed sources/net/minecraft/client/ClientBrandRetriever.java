/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import me.nzxtercode.bettercraft.client.Config;

public class ClientBrandRetriever {
    public static String getClientModName() {
        return Config.getInstance().getConfig("Brand").getAsJsonObject().get("name").getAsString();
    }
}

