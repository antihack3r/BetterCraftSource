/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import java.nio.FloatBuffer;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class CosmeticGalaxySkin
extends CosmeticBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/end_portal.png");
    private final RenderPlayer renderPlayer;
    private ModelPlayer playerModel;
    FloatBuffer field_147528_b = GLAllocation.createDirectFloatBuffer(16);

    public CosmeticGalaxySkin(RenderPlayer renderPlayer) {
        super(renderPlayer);
        this.renderPlayer = renderPlayer;
        this.playerModel = renderPlayer.getMainModel();
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(1, 1);
        GlStateManager.texGen(GlStateManager.TexGen.S, 9217);
        GlStateManager.texGen(GlStateManager.TexGen.T, 9217);
        GlStateManager.texGen(GlStateManager.TexGen.R, 9217);
        GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
        GlStateManager.texGen(GlStateManager.TexGen.S, 9473, this.doFloatBuffer(1.0f, 0.0f, 0.0f, 0.0f));
        GlStateManager.texGen(GlStateManager.TexGen.T, 9473, this.doFloatBuffer(0.0f, 0.0f, 1.0f, 0.0f));
        GlStateManager.texGen(GlStateManager.TexGen.R, 9473, this.doFloatBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, this.doFloatBuffer(0.0f, 1.0f, 0.0f, 0.0f));
        GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
        this.playerModel = new ModelPlayer(0.4f, false);
        this.renderPlayer.bindTexture(TEXTURE);
        this.playerModel.setModelAttributes(this.renderPlayer.getMainModel());
        this.playerModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.enableLighting();
    }

    private FloatBuffer doFloatBuffer(float f2, float g2, float h2, float i2) {
        this.field_147528_b.clear();
        this.field_147528_b.put(f2).put(g2).put(h2).put(i2);
        this.field_147528_b.flip();
        return this.field_147528_b;
    }

    @Override
    public int getId() {
        return 12;
    }
}

