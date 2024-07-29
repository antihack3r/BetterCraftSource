/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

public interface PopUpCallback {
    public void cancel();

    public void ok();

    public void ok(int var1, String var2);

    public boolean tick(int var1);
}

