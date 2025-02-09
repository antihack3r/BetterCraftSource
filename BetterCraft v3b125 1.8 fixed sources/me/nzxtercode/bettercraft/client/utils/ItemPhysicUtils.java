/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.utils;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ItemPhysicUtils {
    public static Random random = new Random();
    public static Minecraft mc = Minecraft.getMinecraft();
    public static RenderItem renderItem = mc.getRenderItem();
    public static long tick;
    public static double rotation;
    public static final ResourceLocation RES_ITEM_GLINT;

    static {
        RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    }

    public static void doRender(Entity par1Entity, double x2, double y2, double z2, float par8, float par9) {
        EntityItem entityitem;
        ItemStack itemstack;
        rotation = (double)(System.nanoTime() - tick) / 3000000.0 * 0.1;
        if (!ItemPhysicUtils.mc.inGameHasFocus) {
            rotation = 0.0;
        }
        if ((itemstack = (entityitem = (EntityItem)par1Entity).getEntityItem()).getItem() != null) {
            random.setSeed(187L);
            boolean flag = false;
            if (TextureMap.locationBlocksTexture != null) {
                ItemPhysicUtils.mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                ItemPhysicUtils.mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
                flag = true;
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            IBakedModel ibakedmodel = renderItem.getItemModelMesher().getItemModel(itemstack);
            int i2 = ItemPhysicUtils.func_177077_a(entityitem, x2, y2, z2, par9, ibakedmodel);
            BlockPos blockpos = new BlockPos(entityitem);
            if (entityitem.rotationPitch > 360.0f) {
                entityitem.rotationPitch = 0.0f;
            }
            if (!(entityitem == null || Double.isNaN(entityitem.posX) || Double.isNaN(entityitem.posY) || Double.isNaN(entityitem.posZ) || Minecraft.getMinecraft().theWorld == null)) {
                if (entityitem.onGround) {
                    if (entityitem.rotationPitch != 0.0f && entityitem.rotationPitch != 90.0f && entityitem.rotationPitch != 180.0f && entityitem.rotationPitch != 270.0f) {
                        double d0 = ItemPhysicUtils.formPositiv(entityitem.rotationPitch);
                        double d1 = ItemPhysicUtils.formPositiv(entityitem.rotationPitch - 90.0f);
                        double d2 = ItemPhysicUtils.formPositiv(entityitem.rotationPitch - 180.0f);
                        double d3 = ItemPhysicUtils.formPositiv(entityitem.rotationPitch - 270.0f);
                        if (d0 <= d1 && d0 <= d2 && d0 <= d3) {
                            entityitem.rotationPitch = entityitem.rotationPitch < 0.0f ? (float)((double)entityitem.rotationPitch + rotation) : (float)((double)entityitem.rotationPitch - rotation);
                        }
                        if (d1 < d0 && d1 <= d2 && d1 <= d3) {
                            entityitem.rotationPitch = entityitem.rotationPitch - 90.0f < 0.0f ? (float)((double)entityitem.rotationPitch + rotation) : (float)((double)entityitem.rotationPitch - rotation);
                        }
                        if (d2 < d1 && d2 < d0 && d2 <= d3) {
                            entityitem.rotationPitch = entityitem.rotationPitch - 180.0f < 0.0f ? (float)((double)entityitem.rotationPitch + rotation) : (float)((double)entityitem.rotationPitch - rotation);
                        }
                        if (d3 < d1 && d3 < d2 && d3 < d0) {
                            entityitem.rotationPitch = entityitem.rotationPitch - 270.0f < 0.0f ? (float)((double)entityitem.rotationPitch + rotation) : (float)((double)entityitem.rotationPitch - rotation);
                        }
                    }
                } else {
                    BlockPos blockpos1 = new BlockPos(entityitem);
                    blockpos1.add(0, 1, 0);
                    Material material = Minecraft.getMinecraft().theWorld.getBlockState(blockpos1).getBlock().getMaterial();
                    Material material1 = Minecraft.getMinecraft().theWorld.getBlockState(blockpos).getBlock().getMaterial();
                    boolean flag1 = entityitem.isInsideOfMaterial(Material.water);
                    boolean flag2 = entityitem.isInWater();
                    entityitem.rotationPitch = flag1 | material == Material.water | material1 == Material.water | flag2 ? (float)((double)entityitem.rotationPitch + rotation / 4.0) : (float)((double)entityitem.rotationPitch + rotation * 3.0);
                }
            }
            GL11.glRotatef(entityitem.rotationYaw, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(entityitem.rotationPitch + 90.0f, 1.0f, 0.0f, 0.0f);
            int j2 = 0;
            while (j2 < i2) {
                if (ibakedmodel.isAmbientOcclusion()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.6f, 0.6f, 0.6f);
                    renderItem.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.6f, 0.6f, 0.6f);
                    if (j2 > 0 && ItemPhysicUtils.shouldSpreadItems()) {
                        GlStateManager.translate(0.0f, 0.0f, 0.046875f * (float)j2);
                    }
                    renderItem.renderItem(itemstack, ibakedmodel);
                    if (!ItemPhysicUtils.shouldSpreadItems()) {
                        GlStateManager.translate(0.0f, 0.0f, 0.046875f);
                    }
                    GlStateManager.popMatrix();
                }
                ++j2;
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            ItemPhysicUtils.mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            if (flag) {
                ItemPhysicUtils.mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
            }
        }
    }

    public static int func_177077_a(EntityItem item, double x2, double y2, double z2, float p_177077_8_, IBakedModel p_177077_9_) {
        ItemStack itemstack = item.getEntityItem();
        Item item1 = itemstack.getItem();
        if (item1 == null) {
            return 0;
        }
        boolean flag = p_177077_9_.isAmbientOcclusion();
        int i2 = ItemPhysicUtils.func_177078_a(itemstack);
        float f2 = 0.25f;
        float f1 = 0.0f;
        GlStateManager.translate((float)x2, (float)y2 + f1 + 0.25f, (float)z2);
        float f22 = 0.0f;
        if (flag || ItemPhysicUtils.mc.getRenderManager().options != null && ItemPhysicUtils.mc.getRenderManager().options.fancyGraphics) {
            GlStateManager.rotate(f22, 0.0f, 1.0f, 0.0f);
        }
        if (!flag) {
            f22 = -0.0f * (float)(i2 - 1) * 0.5f;
            float f3 = -0.0f * (float)(i2 - 1) * 0.5f;
            float f4 = -0.046875f * (float)(i2 - 1) * 0.5f;
            GlStateManager.translate(f22, f3, f4);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        return i2;
    }

    public static boolean shouldSpreadItems() {
        return true;
    }

    public static double formPositiv(float rotationPitch) {
        return rotationPitch > 0.0f ? (double)rotationPitch : (double)(-rotationPitch);
    }

    public static int func_177078_a(ItemStack stack) {
        int b0 = 1;
        if (stack.stackSize > 48) {
            b0 = 5;
        } else if (stack.stackSize > 32) {
            b0 = 4;
        } else if (stack.stackSize > 16) {
            b0 = 3;
        } else if (stack.stackSize > 1) {
            b0 = 2;
        }
        return b0;
    }

    public static byte getMiniBlockCount(ItemStack stack, byte original) {
        return original;
    }

    public static byte getMiniItemCount(ItemStack stack, byte original) {
        return original;
    }
}

