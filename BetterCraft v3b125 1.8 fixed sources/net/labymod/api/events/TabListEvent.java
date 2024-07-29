/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.events;

public interface TabListEvent {
    public void onUpdate(Type var1, String var2, String var3);

    public static enum Type {
        HEADER,
        FOOTER;

    }
}

