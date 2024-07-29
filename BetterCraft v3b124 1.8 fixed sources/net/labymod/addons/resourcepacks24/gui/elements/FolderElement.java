/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.elements;

import java.io.File;
import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.LocalPackElement;
import net.labymod.addons.resourcepacks24.gui.elements.basement.DeletableElement;
import net.labymod.gui.elements.GuiImageButton;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FolderElement
extends DeletableElement {
    public static final ResourceLocation TEXTURE_FOLDER = new ResourceLocation("resourcepacks24/textures/folder.png");
    private File directory;
    private boolean expanded;
    private GuiImageButton buttonFolder;
    private GuiImageButton buttonArrow;

    public FolderElement(File directory) {
        super(true);
        this.directory = directory;
        this.buttonFolder = new GuiImageButton(TEXTURE_FOLDER, 0, 0, 85, 127);
        this.buttonArrow = new GuiImageButton(TEXTURE_FOLDER, 85, 0, 85, 127, 85, 127){

            @Override
            public boolean isMouseOver(int mouseX, int mouseY) {
                return FolderElement.this.expanded;
            }
        };
    }

    @Override
    protected String getElementName() {
        return this.directory.getName();
    }

    @Override
    public boolean draw(double x2, double y2, double width, double height, int mouseX, int mouseY) {
        boolean mouseOver = super.draw(x2, y2, width, height, mouseX, mouseY);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int padding = 3;
        if (mouseOver) {
            DrawUtils.drawRect(x2, y2 - 1.0, x2 + width, y2 + height + 1.0, ModColor.toRGB(100, 100, 100, 35));
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE_FOLDER);
        this.buttonArrow.draw(x2 + 3.0, y2, height, mouseX, mouseY);
        this.buttonFolder.draw(x2 + 6.0 + height, y2, height, mouseX, mouseY);
        draw.drawString(this.directory.getName(), x2 + height + 9.0 + height, y2 + 1.0);
        return mouseOver;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton, GuiResourcepacks24 gui) {
        super.mouseClicked(mouseX, mouseY, mouseButton, gui);
        if (!this.buttonDelete.isMouseOver(mouseX, mouseY)) {
            this.expanded = !this.expanded;
        }
    }

    public boolean equals(Object obj) {
        return ((FolderElement)obj).getDirectory().equals(this.directory);
    }

    public boolean contains(LocalPackElement meta) {
        return meta.getPackMeta().file.getParentFile().equals(this.directory);
    }

    public File getDirectory() {
        return this.directory;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public GuiImageButton getButtonFolder() {
        return this.buttonFolder;
    }

    public GuiImageButton getButtonArrow() {
        return this.buttonArrow;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}

