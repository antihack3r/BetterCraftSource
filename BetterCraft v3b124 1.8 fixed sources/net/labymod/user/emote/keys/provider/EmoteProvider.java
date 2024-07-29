/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote.keys.provider;

import net.labymod.user.emote.keys.PoseAtTime;

public abstract class EmoteProvider {
    public abstract boolean hasNext(int var1);

    public abstract boolean isWaiting();

    public abstract PoseAtTime next(int var1);

    public abstract void clear();
}

