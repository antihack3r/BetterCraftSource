/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import net.labymod.mojang.RenderPlayerHook;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public interface RenderPlayerAdapter {
    public String[] getSkinMapNames();

    public LayerRenderer[] getLayerRenderers(RenderPlayer var1);

    public void renderName(RenderPlayerHook.RenderPlayerCustom var1, AbstractClientPlayer var2, double var3, double var5, double var7);

    public RenderPlayerHook.RenderPlayerCustom getRenderPlayer(RenderManager var1, boolean var2);
}

