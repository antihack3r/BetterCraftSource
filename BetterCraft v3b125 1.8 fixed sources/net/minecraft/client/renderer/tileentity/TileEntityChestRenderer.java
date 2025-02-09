/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.tileentity;

import java.awt.Color;
import java.util.Calendar;
import me.nzxtercode.bettercraft.client.gui.section.GuiMisc;
import me.nzxtercode.bettercraft.client.utils.OutlineUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityChestRenderer
extends TileEntitySpecialRenderer<TileEntityChest> {
    private static final ResourceLocation textureTrappedDouble = new ResourceLocation("textures/entity/chest/trapped_double.png");
    private static final ResourceLocation textureChristmasDouble = new ResourceLocation("textures/entity/chest/christmas_double.png");
    private static final ResourceLocation textureNormalDouble = new ResourceLocation("textures/entity/chest/normal_double.png");
    private static final ResourceLocation textureTrapped = new ResourceLocation("textures/entity/chest/trapped.png");
    private static final ResourceLocation textureChristmas = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation textureNormal = new ResourceLocation("textures/entity/chest/normal.png");
    private ModelChest simpleChest = new ModelChest();
    private ModelChest largeChest = new ModelLargeChest();
    private boolean isChristmas;

    public TileEntityChestRenderer() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.isChristmas = true;
        }
    }

    @Override
    public void renderTileEntityAt(TileEntityChest te2, double x2, double y2, double z2, float partialTicks, int destroyStage) {
        int i2;
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        if (!te2.hasWorldObj()) {
            i2 = 0;
        } else {
            Block block = te2.getBlockType();
            i2 = te2.getBlockMetadata();
            if (block instanceof BlockChest && i2 == 0) {
                ((BlockChest)block).checkForSurroundingChests(te2.getWorld(), te2.getPos(), te2.getWorld().getBlockState(te2.getPos()));
                i2 = te2.getBlockMetadata();
            }
            te2.checkForAdjacentChests();
        }
        if (te2.adjacentChestZNeg == null && te2.adjacentChestXNeg == null) {
            float f2;
            float f1;
            ModelChest modelchest;
            if (te2.adjacentChestXPos == null && te2.adjacentChestZPos == null) {
                modelchest = this.simpleChest;
                if (destroyStage >= 0) {
                    this.bindTexture(DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(4.0f, 4.0f, 1.0f);
                    GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
                    GlStateManager.matrixMode(5888);
                } else if (this.isChristmas) {
                    this.bindTexture(textureChristmas);
                } else if (te2.getChestType() == 1) {
                    this.bindTexture(textureTrapped);
                } else {
                    this.bindTexture(textureNormal);
                }
            } else {
                modelchest = this.largeChest;
                if (destroyStage >= 0) {
                    this.bindTexture(DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0f, 4.0f, 1.0f);
                    GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
                    GlStateManager.matrixMode(5888);
                } else if (this.isChristmas) {
                    this.bindTexture(textureChristmasDouble);
                } else if (te2.getChestType() == 1) {
                    this.bindTexture(textureTrappedDouble);
                } else {
                    this.bindTexture(textureNormalDouble);
                }
            }
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            if (destroyStage < 0) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            GlStateManager.translate((float)x2, (float)y2 + 1.0f, (float)z2 + 1.0f);
            GlStateManager.scale(1.0f, -1.0f, -1.0f);
            GlStateManager.translate(0.5f, 0.5f, 0.5f);
            int j2 = 0;
            if (i2 == 2) {
                j2 = 180;
            }
            if (i2 == 3) {
                j2 = 0;
            }
            if (i2 == 4) {
                j2 = 90;
            }
            if (i2 == 5) {
                j2 = -90;
            }
            if (i2 == 2 && te2.adjacentChestXPos != null) {
                GlStateManager.translate(1.0f, 0.0f, 0.0f);
            }
            if (i2 == 5 && te2.adjacentChestZPos != null) {
                GlStateManager.translate(0.0f, 0.0f, -1.0f);
            }
            GlStateManager.rotate(j2, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5f, -0.5f, -0.5f);
            float f3 = te2.prevLidAngle + (te2.lidAngle - te2.prevLidAngle) * partialTicks;
            if (te2.adjacentChestZNeg != null && (f1 = te2.adjacentChestZNeg.prevLidAngle + (te2.adjacentChestZNeg.lidAngle - te2.adjacentChestZNeg.prevLidAngle) * partialTicks) > f3) {
                f3 = f1;
            }
            if (te2.adjacentChestXNeg != null && (f2 = te2.adjacentChestXNeg.prevLidAngle + (te2.adjacentChestXNeg.lidAngle - te2.adjacentChestXNeg.prevLidAngle) * partialTicks) > f3) {
                f3 = f2;
            }
            f3 = 1.0f - f3;
            f3 = 1.0f - f3 * f3 * f3;
            modelchest.chestLid.rotateAngleX = -(f3 * (float)Math.PI / 2.0f);
            modelchest.renderAll();
            if (GuiMisc.enabledESP[2]) {
                modelchest.renderAll();
                OutlineUtils.renderOne();
                modelchest.renderAll();
                OutlineUtils.renderTwo();
                modelchest.renderAll();
                OutlineUtils.renderThree();
                OutlineUtils.setColor(new Color(0.0f, 0.0f, 0.0f, 0.1f));
                OutlineUtils.renderFour();
                modelchest.renderAll();
                OutlineUtils.renderFive();
            }
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (destroyStage >= 0) {
                GlStateManager.matrixMode(5890);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
            }
        }
    }
}

