/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.util;

import net.labymod.user.User;

public abstract class CosmeticData {
    public abstract boolean isEnabled();

    public abstract void loadData(String[] var1) throws Exception;

    public void init(User user) {
    }

    public void completed(User user) {
    }
}

