/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import net.minecraft.client.renderer.vertex.VertexFormat;

public interface WorldRendererAdapter {
    public void begin(int var1, VertexFormat var2);

    public WorldRendererAdapter pos(double var1, double var3, double var5);

    public WorldRendererAdapter tex(double var1, double var3);

    public WorldRendererAdapter color(float var1, float var2, float var3, float var4);

    public WorldRendererAdapter color(int var1, int var2, int var3, int var4);

    public void endVertex();
}

