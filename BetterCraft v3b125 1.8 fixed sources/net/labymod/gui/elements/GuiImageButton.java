/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class GuiImageButton
extends Gui {
    private final ResourceLocation imageResource;
    private ResourceLocation hoverImageResource;
    private int offsetX;
    private int offsetY;
    private int hoverOffsetX;
    private int hoverOffsetY;
    private int textureSizeX;
    private int textureSizeY;
    private double x;
    private double y;
    private double width;
    private double height;
    private double imageHoverZoom = 0.5;
    private float imageAlpha = 1.0f;
    private float imageAlphaHover = 1.0f;
    private boolean enabled = true;

    public GuiImageButton(ResourceLocation imageResource, ResourceLocation hoverImageResource) {
        this.imageResource = imageResource;
        this.hoverImageResource = hoverImageResource;
    }

    public GuiImageButton(ResourceLocation imageResource) {
        this(imageResource, imageResource);
    }

    public GuiImageButton(ResourceLocation imageResource, int offsetX, int offsetY, int hoverOffsetX, int hoverOffsetY, int textureSizeX, int textureSizeY) {
        this.imageResource = imageResource;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.hoverOffsetX = hoverOffsetX;
        this.hoverOffsetY = hoverOffsetY;
        this.textureSizeX = textureSizeX;
        this.textureSizeY = textureSizeY;
    }

    public GuiImageButton(ResourceLocation imageResource, int offsetX, int offsetY, int textureSizeX, int textureSizeY) {
        this(imageResource, offsetX, offsetY, offsetX, offsetY, textureSizeX, textureSizeY);
    }

    public void init(double x2, double y2, double width, double height) {
        this.x = x2;
        this.y = y2;
        this.width = width;
        this.height = height;
    }

    public void init(double x2, double y2, double size) {
        this.init(x2, y2, size, size);
    }

    public void draw(double x2, double y2, double width, double height, int mouseX, int mouseY) {
        this.init(x2, y2, width, height);
        this.draw(mouseX, mouseY);
    }

    public void draw(double x2, double y2, double size, int mouseX, int mouseY) {
        this.init(x2, y2, size);
        this.draw(mouseX, mouseY);
    }

    public void draw(int mouseX, int mouseY) {
        boolean mouseOver = this.isMouseOver(mouseX, mouseY);
        double zoom = mouseOver ? this.imageHoverZoom : 0.0;
        boolean usingTextureMap = this.hoverImageResource == null;
        Minecraft.getMinecraft().getTextureManager().bindTexture(mouseOver && !usingTextureMap ? this.hoverImageResource : this.imageResource);
        if (usingTextureMap) {
            LabyMod.getInstance().getDrawUtils().drawTexture(this.x - zoom, this.y - zoom, mouseOver ? (double)this.hoverOffsetX : (double)this.offsetX, mouseOver ? (double)this.hoverOffsetY : (double)this.offsetY, this.textureSizeX, this.textureSizeY, this.width + zoom * 2.0, this.height + zoom * 2.0, mouseOver ? this.imageAlphaHover : this.imageAlpha);
        } else {
            LabyMod.getInstance().getDrawUtils().drawTexture(this.x - zoom, this.y - zoom, 255.0, 255.0, this.width + zoom * 2.0, this.height + zoom * 2.0, mouseOver ? this.imageAlphaHover : this.imageAlpha);
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return (double)mouseX > this.x && (double)mouseX < this.x + this.width && (double)mouseY > this.y && (double)mouseY < this.y + this.height && this.enabled;
    }

    public void setImageHoverZoom(double imageHoverZoom) {
        this.imageHoverZoom = imageHoverZoom;
    }

    public void setImageAlpha(float imageAlpha) {
        this.imageAlpha = imageAlpha;
    }

    public void setImageAlphaHover(float imageAlphaHover) {
        this.imageAlphaHover = imageAlphaHover;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

