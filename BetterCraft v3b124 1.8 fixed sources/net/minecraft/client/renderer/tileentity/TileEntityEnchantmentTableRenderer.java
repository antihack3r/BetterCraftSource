/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnchantmentTableRenderer
extends TileEntitySpecialRenderer<TileEntityEnchantmentTable> {
    private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private ModelBook field_147541_c = new ModelBook();

    @Override
    public void renderTileEntityAt(TileEntityEnchantmentTable te2, double x2, double y2, double z2, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x2 + 0.5f, (float)y2 + 0.75f, (float)z2 + 0.5f);
        float f2 = (float)te2.tickCount + partialTicks;
        GlStateManager.translate(0.0f, 0.1f + MathHelper.sin(f2 * 0.1f) * 0.01f, 0.0f);
        float f1 = te2.bookRotation - te2.bookRotationPrev;
        while (f1 >= (float)Math.PI) {
            f1 -= (float)Math.PI * 2;
        }
        while (f1 < (float)(-Math.PI)) {
            f1 += (float)Math.PI * 2;
        }
        float f22 = te2.bookRotationPrev + f1 * partialTicks;
        GlStateManager.rotate(-f22 * 180.0f / (float)Math.PI, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(80.0f, 0.0f, 0.0f, 1.0f);
        this.bindTexture(TEXTURE_BOOK);
        float f3 = te2.pageFlipPrev + (te2.pageFlip - te2.pageFlipPrev) * partialTicks + 0.25f;
        float f4 = te2.pageFlipPrev + (te2.pageFlip - te2.pageFlipPrev) * partialTicks + 0.75f;
        f3 = (f3 - (float)MathHelper.truncateDoubleToInt(f3)) * 1.6f - 0.3f;
        f4 = (f4 - (float)MathHelper.truncateDoubleToInt(f4)) * 1.6f - 0.3f;
        if (f3 < 0.0f) {
            f3 = 0.0f;
        }
        if (f4 < 0.0f) {
            f4 = 0.0f;
        }
        if (f3 > 1.0f) {
            f3 = 1.0f;
        }
        if (f4 > 1.0f) {
            f4 = 1.0f;
        }
        float f5 = te2.bookSpreadPrev + (te2.bookSpread - te2.bookSpreadPrev) * partialTicks;
        GlStateManager.enableCull();
        this.field_147541_c.render(null, f2, f3, f4, f5, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
}

