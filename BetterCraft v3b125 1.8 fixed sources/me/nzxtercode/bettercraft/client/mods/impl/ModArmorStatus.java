/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods.impl;

import me.nzxtercode.bettercraft.client.hud.ScreenPosition;
import me.nzxtercode.bettercraft.client.mods.ModRender;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ModArmorStatus
extends ModRender {
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
        this.renderItemStack(pos, 4, this.mc.thePlayer.inventory.getCurrentItem());
        this.font.drawString(new String(), 0, 0, -1);
        int i2 = 0;
        while (i2 < this.mc.thePlayer.inventory.armorInventory.length) {
            ItemStack itemStack = this.mc.thePlayer.inventory.armorInventory[i2];
            this.renderItemStack(pos, i2, itemStack);
            ++i2;
        }
    }

    @Override
    public void renderDummy(ScreenPosition pos) {
        this.renderItemStack(pos, 4, new ItemStack(Items.diamond_sword));
        this.renderItemStack(pos, 3, new ItemStack(Items.diamond_helmet));
        this.renderItemStack(pos, 2, new ItemStack(Items.diamond_chestplate));
        this.renderItemStack(pos, 1, new ItemStack(Items.diamond_leggings));
        this.renderItemStack(pos, 0, new ItemStack(Items.diamond_boots));
    }

    private void renderItemStack(ScreenPosition pos, int i2, ItemStack is2) {
        if (is2 == null) {
            return;
        }
        GL11.glPushMatrix();
        int yAdd = -16 * i2 + 68;
        if (is2.getItem().isDamageable()) {
            double damage = (double)(is2.getMaxDamage() - is2.getItemDamage()) / (double)is2.getMaxDamage() * 100.0;
            this.font.drawString(String.format("%.2f%%", damage), pos.getAbsoluteX() + 22, pos.getAbsoluteY() + yAdd + 5, ColorUtils.rainbowEffect());
        }
        RenderHelper.enableGUIStandardItemLighting();
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(is2, pos.getAbsoluteX() + 4, pos.getAbsoluteY() + yAdd);
        GL11.glPopMatrix();
    }
}

