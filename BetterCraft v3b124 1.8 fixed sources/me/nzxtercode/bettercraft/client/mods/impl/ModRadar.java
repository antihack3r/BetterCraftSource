/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods.impl;

import me.nzxtercode.bettercraft.client.hud.ScreenPosition;
import me.nzxtercode.bettercraft.client.mods.ModRender;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class ModRadar
extends ModRender {
    @Override
    public int getWidth() {
        return 80;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public void render(ScreenPosition pos) {
        GL11.glPushMatrix();
        int x1 = pos.getAbsoluteX();
        int x2 = pos.getAbsoluteX() + 80;
        int y1 = pos.getAbsoluteY();
        int y2 = pos.getAbsoluteY() + 80;
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(2929);
        GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.5f);
        GL11.glBegin(7);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x1, y2);
        GL11.glEnd();
        GL11.glLineWidth(2.0f);
        GL11.glBegin(2);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x1, y2);
        GL11.glEnd();
        GL11.glColor4f(0.1f, 1.0f, 0.1f, 1.0f);
        GL11.glLineWidth(1.0f);
        float prevRotationYawHead = Minecraft.getMinecraft().thePlayer.prevRotationYawHead;
        float rotationYawHead = Minecraft.getMinecraft().thePlayer.rotationYawHead;
        float n2 = rotationYawHead - Minecraft.getMinecraft().thePlayer.prevRotationYawHead;
        float rotation = -(prevRotationYawHead + n2 * Minecraft.getMinecraft().timer.renderPartialTicks);
        for (Entity en2 : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (en2.isInvisible()) {
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            } else {
                if (!(en2 instanceof EntityLivingBase)) continue;
                if (en2 instanceof EntityPlayer) {
                    if (en2 != Minecraft.getMinecraft().thePlayer) {
                        if (Minecraft.getMinecraft().thePlayer.isOnSameTeam((EntityLivingBase)en2)) {
                            GL11.glColor4f(0.5f, 1.0f, 0.5f, 1.0f);
                        } else {
                            GL11.glColor4f(1.0f, 1.0f, 0.4f, 1.0f);
                        }
                    } else {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
                if (en2 instanceof IMob) {
                    GL11.glColor4f(1.0f, 0.1f, 0.5f, 1.0f);
                }
                if (en2 instanceof EntityAnimal) {
                    GL11.glColor4f(1.0f, 1.0f, 0.5f, 1.0f);
                }
                if (en2 instanceof INpc || en2 instanceof EntityIronGolem) {
                    GL11.glColor4f(1.0f, 0.5f, 1.0f, 1.0f);
                }
            }
            GL11.glTranslated((x1 + x2) / 2, (double)(y1 + y2 / 2) - (double)(y1 / 2), 0.0);
            GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            double posX2 = Minecraft.getMinecraft().thePlayer.posX;
            double lastTickPosX = en2.lastTickPosX;
            double n22 = en2.posX - en2.lastTickPosX;
            double posX = -(posX2 - (lastTickPosX + n22 * (double)Minecraft.getMinecraft().timer.renderPartialTicks)) / 3.1;
            double posZ2 = Minecraft.getMinecraft().thePlayer.posZ;
            double lastTickPosZ = en2.lastTickPosZ;
            double n3 = en2.posZ - en2.lastTickPosZ;
            double posZ = -(posZ2 - (lastTickPosZ + n3 * (double)Minecraft.getMinecraft().timer.renderPartialTicks)) / 3.1;
            double posY2 = Minecraft.getMinecraft().thePlayer.posY;
            double lastTickPosY = en2.lastTickPosY;
            double n4 = en2.posY - en2.lastTickPosY;
            double posY = -(posY2 - (lastTickPosY + n4 * (double)Minecraft.getMinecraft().timer.renderPartialTicks)) / 500.0;
            GL11.glPushMatrix();
            GL11.glScaled(1.4 + posY, 1.4 + posY, 1.0);
            GL11.glBegin(6);
            int i2 = 0;
            while (i2 <= 10) {
                double angle = Math.PI * 2 * (double)i2 / 10.0;
                double x3 = Math.cos(angle);
                double y3 = Math.sin(angle);
                GL11.glVertex3d(x3 + posX, y3 + posZ, 365.0 + posY);
                ++i2;
            }
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(-180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glTranslated(-((x1 + x2) / 2), -((y1 + y2) / 2), 0.0);
        }
        GL11.glPopMatrix();
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(2929);
    }
}

