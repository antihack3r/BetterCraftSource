/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class TileEntitySignRenderer
extends TileEntitySpecialRenderer<TileEntitySign> {
    private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");
    private final ModelSign model = new ModelSign();
    private static double textRenderDistanceSq = 4096.0;

    @Override
    public void renderTileEntityAt(TileEntitySign te2, double x2, double y2, double z2, float partialTicks, int destroyStage) {
        Block block = te2.getBlockType();
        GlStateManager.pushMatrix();
        float f2 = 0.6666667f;
        if (block == Blocks.standing_sign) {
            GlStateManager.translate((float)x2 + 0.5f, (float)y2 + 0.75f * f2, (float)z2 + 0.5f);
            float f1 = (float)(te2.getBlockMetadata() * 360) / 16.0f;
            GlStateManager.rotate(-f1, 0.0f, 1.0f, 0.0f);
            this.model.signStick.showModel = true;
        } else {
            int k2 = te2.getBlockMetadata();
            float f22 = 0.0f;
            if (k2 == 2) {
                f22 = 180.0f;
            }
            if (k2 == 4) {
                f22 = 90.0f;
            }
            if (k2 == 5) {
                f22 = -90.0f;
            }
            GlStateManager.translate((float)x2 + 0.5f, (float)y2 + 0.75f * f2, (float)z2 + 0.5f);
            GlStateManager.rotate(-f22, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -0.3125f, -0.4375f);
            this.model.signStick.showModel = false;
        }
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 2.0f, 1.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(SIGN_TEXTURE);
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scale(f2, -f2, -f2);
        this.model.renderSign();
        GlStateManager.popMatrix();
        if (TileEntitySignRenderer.isRenderText(te2)) {
            FontRenderer fontrenderer = this.getFontRenderer();
            float f3 = 0.015625f * f2;
            GlStateManager.translate(0.0f, 0.5f * f2, 0.07f * f2);
            GlStateManager.scale(f3, -f3, f3);
            GL11.glNormal3f(0.0f, 0.0f, -1.0f * f3);
            GlStateManager.depthMask(false);
            int i2 = 0;
            if (Config.isCustomColors()) {
                i2 = CustomColors.getSignTextColor(i2);
            }
            if (destroyStage < 0) {
                int j2 = 0;
                while (j2 < te2.signText.length) {
                    if (te2.signText[j2] != null) {
                        String s2;
                        IChatComponent ichatcomponent = te2.signText[j2];
                        List<IChatComponent> list = GuiUtilRenderComponents.splitText(ichatcomponent, 90, fontrenderer, false, true);
                        String string = s2 = list != null && list.size() > 0 ? list.get(0).getFormattedText() : "";
                        if (j2 == te2.lineBeingEdited) {
                            s2 = "> " + s2 + " <";
                            fontrenderer.drawString(s2, -fontrenderer.getStringWidth(s2) / 2, j2 * 10 - te2.signText.length * 5, i2);
                        } else {
                            fontrenderer.drawString(s2, -fontrenderer.getStringWidth(s2) / 2, j2 * 10 - te2.signText.length * 5, i2);
                        }
                    }
                    ++j2;
                }
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    private static boolean isRenderText(TileEntitySign p_isRenderText_0_) {
        if (Shaders.isShadowPass) {
            return false;
        }
        if (Config.getMinecraft().currentScreen instanceof GuiEditSign) {
            return true;
        }
        if (!Config.zoomMode && p_isRenderText_0_.lineBeingEdited < 0) {
            Entity entity = Config.getMinecraft().getRenderViewEntity();
            double d0 = p_isRenderText_0_.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (d0 > textRenderDistanceSq) {
                return false;
            }
        }
        return true;
    }

    public static void updateTextRenderDistance() {
        Minecraft minecraft = Config.getMinecraft();
        double d0 = Config.limit(minecraft.gameSettings.fovSetting, 1.0f, 120.0f);
        double d1 = Math.max(1.5 * (double)minecraft.displayHeight / d0, 16.0);
        textRenderDistanceSq = d1 * d1;
    }
}

