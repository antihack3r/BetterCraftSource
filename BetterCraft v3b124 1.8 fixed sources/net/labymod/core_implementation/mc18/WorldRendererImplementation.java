/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18;

import net.labymod.core.WorldRendererAdapter;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class WorldRendererImplementation
implements WorldRendererAdapter {
    private static WorldRendererImplementation instance = new WorldRendererImplementation();
    private final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

    public static WorldRendererImplementation getInstance() {
        return instance;
    }

    @Override
    public void begin(int glMode, VertexFormat vertexFormat) {
        this.worldRenderer.begin(glMode, vertexFormat);
    }

    @Override
    public WorldRendererAdapter pos(double x2, double y2, double z2) {
        this.worldRenderer.pos(x2, y2, z2);
        return this;
    }

    @Override
    public WorldRendererAdapter tex(double u2, double v2) {
        this.worldRenderer.tex(u2, v2);
        return this;
    }

    @Override
    public WorldRendererAdapter color(float red, float green, float blue, float alpha) {
        this.worldRenderer.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public WorldRendererAdapter color(int red, int green, int blue, int alpha) {
        this.worldRenderer.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public void endVertex() {
        this.worldRenderer.endVertex();
    }
}

