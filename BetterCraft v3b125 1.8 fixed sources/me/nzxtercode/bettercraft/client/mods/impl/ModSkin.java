/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods.impl;

import me.nzxtercode.bettercraft.client.hud.ScreenPosition;
import me.nzxtercode.bettercraft.client.mods.ModRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import org.lwjgl.opengl.GL11;

public class ModSkin
extends ModRender {
    private static Entity currentEntity;
    public boolean isInIndicator;

    @Override
    public int getWidth() {
        return 64;
    }

    @Override
    public int getHeight() {
        return 84;
    }

    @Override
    public void render(ScreenPosition pos) {
        this.drawEntityOnScreen(pos, 30, 25.0f, 10.0f, currentEntity != null && currentEntity instanceof EntityLivingBase && !ModSkin.currentEntity.isDead ? (EntityLivingBase)currentEntity : this.mc.thePlayer);
    }

    private void drawEntityOnScreen(ScreenPosition pos, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        double entitySize = ent.getEntityBoundingBox().maxY - ent.getEntityBoundingBox().minY;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        double size = ent instanceof EntityIronGolem ? 0.8 : (ent instanceof EntityGiantZombie ? 0.2 : (entitySize >= 3.9 ? 0.3 : (entitySize >= 2.0 ? 0.6 : 1.0)));
        GL11.glScaled(size, size, size);
        GlStateManager.translate(entitySize >= 2.0 ? (double)(pos.getAbsoluteX() + 30) / size : (double)(pos.getAbsoluteX() + 30), entitySize >= 2.0 ? (double)(pos.getAbsoluteY() + 70 - (ent instanceof EntityGhast ? 20 : (ent instanceof EntityGiantZombie ? -10 : 0))) / size : (double)(pos.getAbsoluteY() + 70 - (ent instanceof EntityGhast ? 20 : (ent instanceof EntityGiantZombie ? -10 : 0))), 50.0);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float f2 = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f22 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        this.isInIndicator = true;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f2;
        ent.rotationYaw = f1;
        ent.rotationPitch = f22;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void setCurrentEntity(Entity currentEntity) {
        ModSkin.currentEntity = currentEntity;
    }
}

