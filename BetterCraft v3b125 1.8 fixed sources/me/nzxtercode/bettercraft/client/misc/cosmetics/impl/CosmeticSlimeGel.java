/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class CosmeticSlimeGel
extends CosmeticBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/slime.png");
    private final RenderPlayer renderPlayer;
    private ModelPlayer playerModel;

    public CosmeticSlimeGel(RenderPlayer renderPlayer) {
        super(renderPlayer);
        this.renderPlayer = renderPlayer;
        this.playerModel = renderPlayer.getMainModel();
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.playerModel = new ModelPlayer(0.4f, false);
        this.renderPlayer.bindTexture(TEXTURE);
        Minecraft.getMinecraft().fontRendererObj.setColor(0.5f, 1.5f, 0.5f, 0.5f);
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        this.playerModel.setModelAttributes(this.renderPlayer.getMainModel());
        this.playerModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
    }

    @Override
    public int getId() {
        return 18;
    }
}

