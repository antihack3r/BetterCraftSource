/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics;

import java.io.IOException;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.apache.commons.lang3.StringUtils;

public class GuiCosmetic
extends GuiScreen {
    public GuiScreen before;

    public GuiCosmetic(GuiScreen screen) {
        this.before = screen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1337, width - 93, height - 23, 90, 20, "Back"));
        int index = 0;
        int x2 = 0;
        int y2 = 0;
        while (index < CosmeticInstance.getCosmetics().size()) {
            CosmeticBase cosmetic = CosmeticInstance.getCosmetics().get(index);
            this.buttonList.add(new GuiButton(index, 3 + x2, 3 + y2, 98, 20, String.format("%s%s%s", Character.valueOf('\u00a7'), Character.valueOf(cosmetic.isEnabled() ? (char)'a' : 'c'), StringUtils.join((Object[])StringUtils.splitByCharacterTypeCamelCase(cosmetic.getClass().getSimpleName().replace("Cosmetic", "").replaceAll("\\d+", "")), " "))));
            if (y2 + 56 >= height) {
                y2 = -25;
                x2 += 101;
            }
            ++index;
            y2 += 25;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1337) {
            this.mc.displayGuiScreen(this.before);
        }
        int index = 0;
        while (index < CosmeticInstance.getCosmetics().size()) {
            CosmeticBase cosmetic = CosmeticInstance.getCosmetics().get(index);
            if (button.id == index) {
                cosmetic.setEnabledState(!cosmetic.isEnabled());
                button.displayString = String.format("%s%s%s", Character.valueOf('\u00a7'), Character.valueOf(cosmetic.isEnabled() ? (char)'a' : 'c'), StringUtils.join((Object[])StringUtils.splitByCharacterTypeCamelCase(cosmetic.getClass().getSimpleName().replace("Cosmetic", "").replaceAll("\\d+", "")), " "));
            }
            ++index;
        }
        CosmeticInstance.sendCosmetics();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiCosmetic.drawEntityOnScreen(width - 50, height - 40, 30, (float)(width + 51) - (float)mouseX, (float)(height + 75 - 50) - (float)mouseY, this.mc.thePlayer);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase entity) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 100.0f);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float var5 = entity.prevRenderYawOffset;
        float var6 = entity.renderYawOffset;
        float var7 = entity.rotationYaw;
        float var8 = entity.rotationPitch;
        float var9 = entity.prevRotationYawHead;
        float var10 = entity.rotationYawHead;
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(260.0f + mouseX, 0.0f, 1.0f, 0.0f);
        entity.renderYawOffset = (float)Math.atan(mouseX / 40.0f);
        entity.rotationYaw = (float)Math.atan(mouseX / 40.0f) * 40.0f;
        entity.rotationPitch = (float)Math.atan(mouseY / 40.0f);
        entity.rotationYawHead = -((float)Math.atan(mouseX / 40.0f)) * 10.0f;
        entity.prevRotationYawHead = -((float)Math.atan(mouseX / 40.0f));
        entity.prevRenderYawOffset = -((float)Math.atan(mouseX / 40.0f));
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager var11 = Minecraft.getMinecraft().getRenderManager();
        var11.setPlayerViewY(180.0f);
        var11.setRenderShadow(false);
        var11.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        var11.setRenderShadow(true);
        entity.renderYawOffset = var6;
        entity.rotationYaw = var7;
        entity.rotationPitch = var8;
        entity.prevRenderYawOffset = var5;
        entity.prevRotationYawHead = var9;
        entity.rotationYawHead = var10;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}

