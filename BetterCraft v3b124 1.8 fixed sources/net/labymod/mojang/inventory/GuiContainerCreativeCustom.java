/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.mojang.inventory;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.mojang.inventory.scale.InventoryScaleChanger;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.EntityPlayer;

public class GuiContainerCreativeCustom
extends GuiContainerCreative {
    private InventoryScaleChanger inventoryScaleChanger = new InventoryScaleChanger();
    private boolean hasActivePotionEffects;

    public GuiContainerCreativeCustom(EntityPlayer p_i1094_1_) {
        super(p_i1094_1_);
    }

    @Override
    protected void updateActivePotionEffects() {
        if (!LabyModCore.getMinecraft().getPlayer().getActivePotionEffects().isEmpty()) {
            if (!LabyMod.getSettings().oldInventory) {
                this.guiLeft = 160 + (width - this.xSize - 200) / 2;
            }
            this.hasActivePotionEffects = true;
        } else {
            this.guiLeft = (width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        }
    }

    @Override
    public void initGui() {
        if (this.inventoryScaleChanger.initGui()) {
            width = this.inventoryScaleChanger.getScaledWidth();
            height = this.inventoryScaleChanger.getScaledHeight();
        }
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.inventoryScaleChanger.drawScreen(mouseX, mouseY)) {
            mouseX = this.inventoryScaleChanger.getMouseX();
            mouseY = this.inventoryScaleChanger.getMouseY();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hasActivePotionEffects) {
            LabyModCore.getRenderImplementation().drawActivePotionEffects(this.guiLeft, this.guiTop, LabyModCore.getRenderImplementation().getInventoryBackground());
        }
    }
}
