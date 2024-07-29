/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote.keys.provider;

import java.beans.ConstructorProperties;
import net.labymod.user.emote.keys.EmoteKeyFrame;

public class KeyFrameStorage {
    private short id;
    private String name;
    private long timeout;
    private EmoteKeyFrame[] keyframes;

    public short getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public EmoteKeyFrame[] getKeyframes() {
        return this.keyframes;
    }

    public void setId(short id2) {
        this.id = id2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setKeyframes(EmoteKeyFrame[] keyframes) {
        this.keyframes = keyframes;
    }

    @ConstructorProperties(value={"id", "name", "timeout", "keyframes"})
    public KeyFrameStorage(short id2, String name, long timeout, EmoteKeyFrame[] keyframes) {
        this.id = id2;
        this.name = name;
        this.timeout = timeout;
        this.keyframes = keyframes;
    }
}

