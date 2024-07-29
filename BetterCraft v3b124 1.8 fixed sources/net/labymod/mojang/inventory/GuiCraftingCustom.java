/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.mojang.inventory;

import net.labymod.mojang.inventory.scale.InventoryScaleChanger;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiCraftingCustom
extends GuiCrafting {
    private InventoryScaleChanger inventoryScaleChanger = new InventoryScaleChanger();

    public GuiCraftingCustom(InventoryPlayer playerInv, World worldIn) {
        super(playerInv, worldIn);
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
    }
}

