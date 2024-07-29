/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.elements;

import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.basement.PackElement;
import net.labymod.addons.resourcepacks24.loader.PackRepositoryLoader;
import net.labymod.addons.resourcepacks24.loader.model.PackMeta;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class LocalPackElement
extends PackElement {
    private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
    private PackMeta packMeta;
    private boolean hoverChoose;
    private int hoverSwap;

    public LocalPackElement(PackMeta packMeta) {
        super(true);
        this.packMeta = packMeta;
    }

    @Override
    public String getDisplayName() {
        return this.packMeta.displayName;
    }

    @Override
    public ResourceLocation getIcon() {
        return this.packMeta.icon;
    }

    @Override
    public String getDescription() {
        return this.packMeta.pack.description;
    }

    @Override
    public void drawControls(double x2, double y2, double width, double height, boolean isFirstEntry, boolean isLastEntry, boolean isSelected, int mouseX, int mouseY, GuiResourcepacks24 gui) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        if (gui.getSharedView().draggingElement == null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(RESOURCE_PACKS_TEXTURE);
            this.hoverChoose = (double)mouseX < x2 + height / (double)(isSelected ? 2 : 1);
            draw.drawTexture(x2, y2, isSelected ? 32.0 : 0.0, this.hoverChoose ? 32.0 : 0.0, 32.0, 32.0, height, height);
            if (isSelected) {
                boolean hoverRightSide;
                boolean bl2 = hoverRightSide = (double)mouseX > x2 + height / 2.0 && (double)mouseX < x2 + height;
                int n2 = hoverRightSide ? ((double)mouseY < y2 + height / 2.0 ? (isFirstEntry ? 0 : 1) : (isLastEntry ? 0 : -1)) : (this.hoverSwap = 0);
                if (!isFirstEntry) {
                    draw.drawTexture(x2, y2, 96.0, this.hoverSwap == 1 ? 32.0 : 0.0, 32.0, 32.0, height, height);
                }
                if (!isLastEntry) {
                    draw.drawTexture(x2, y2, 64.0, this.hoverSwap == -1 ? 32.0 : 0.0, 32.0, 32.0, height, height);
                }
            }
        }
        super.drawControls(x2, y2, width, height, isFirstEntry, isLastEntry, isSelected, mouseX, mouseY, gui);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton, GuiResourcepacks24 gui) {
        super.mouseReleased(mouseX, mouseY, mouseButton, gui);
        PackRepositoryLoader packLoader = gui.getResourcepacks24().getPackLoader();
        if (this.hoverChoose) {
            if (packLoader.isSelected(this)) {
                packLoader.unselectPack(this);
            } else {
                packLoader.selectPack(this);
            }
        } else if (this.hoverSwap != 0) {
            packLoader.swap(this, this.hoverSwap);
        }
        gui.initGui();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LocalPackElement) {
            return ((LocalPackElement)obj).getPackMeta().equals(this.packMeta);
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return this.packMeta.hashCode();
    }

    public PackMeta getPackMeta() {
        return this.packMeta;
    }

    public boolean isHoverChoose() {
        return this.hoverChoose;
    }

    public int getHoverSwap() {
        return this.hoverSwap;
    }
}

