/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.mojang;

import java.lang.reflect.Field;
import java.util.Map;
import net.labymod.core.LabyModCore;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.utils.ReflectionHelper;
import net.lenni0451.eventapi.manager.ASMEventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class RenderPlayerHook {
    public RenderPlayerHook() {
        try {
            Field skinMapField = ReflectionHelper.findField(RenderManager.class, LabyModCore.getRenderPlayerImplementation().getSkinMapNames());
            Map skinMap = (Map)skinMapField.get(Minecraft.getMinecraft().getRenderManager());
            skinMap.put("default", LabyModCore.getRenderPlayerImplementation().getRenderPlayer(Minecraft.getMinecraft().getRenderManager(), false));
            skinMap.put("slim", LabyModCore.getRenderPlayerImplementation().getRenderPlayer(Minecraft.getMinecraft().getRenderManager(), true));
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    public static abstract class RenderPlayerCustom
    extends RenderPlayer {
        public RenderPlayerCustom(RenderManager renderManager, boolean slim) {
            super(renderManager, slim);
            this.layerRenderers.clear();
            this.mainModel = new ModelCosmetics(0.0f, slim);
            ASMEventManager.register(this.mainModel);
            LayerRenderer[] layerRendererArray = LabyModCore.getRenderPlayerImplementation().getLayerRenderers(this);
            int n2 = layerRendererArray.length;
            int n3 = 0;
            while (n3 < n2) {
                LayerRenderer layerRenderer = layerRendererArray[n3];
                this.addLayer(layerRenderer);
                ++n3;
            }
        }

        public abstract boolean canRenderTheName(AbstractClientPlayer var1);

        public abstract void renderLabel(AbstractClientPlayer var1, double var2, double var4, double var6, String var8, float var9, double var10);
    }
}

