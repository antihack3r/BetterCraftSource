/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

public class RenderTntMinecart
extends RenderMinecart<EntityMinecartTNT> {
    public RenderTntMinecart(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void func_180560_a(EntityMinecartTNT minecart, float partialTicks, IBlockState state) {
        int i2 = minecart.getFuseTicks();
        if (i2 > -1 && (float)i2 - partialTicks + 1.0f < 10.0f) {
            float f2 = 1.0f - ((float)i2 - partialTicks + 1.0f) / 10.0f;
            f2 = MathHelper.clamp_float(f2, 0.0f, 1.0f);
            f2 *= f2;
            f2 *= f2;
            float f1 = 1.0f + f2 * 0.3f;
            GlStateManager.scale(f1, f1, f1);
        }
        super.func_180560_a(minecart, partialTicks, state);
        if (i2 > -1 && i2 / 5 % 2 == 0) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 772);
            GlStateManager.color(1.0f, 1.0f, 1.0f, (1.0f - ((float)i2 - partialTicks + 1.0f) / 100.0f) * 0.8f);
            GlStateManager.pushMatrix();
            blockrendererdispatcher.renderBlockBrightness(Blocks.tnt.getDefaultState(), 1.0f);
            GlStateManager.popMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
    }
}

