/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.settings.SettingsCategory;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class CategorySettingsElement
extends SettingsElement {
    private SettingsCategory category;
    private ClickedCallback callback;

    public CategorySettingsElement(SettingsCategory category, ClickedCallback callback) {
        super(category.getTitle(), null);
        this.category = category;
        this.callback = callback;
    }

    @Override
    public void init() {
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        double textureSize = 292.0;
        double textureScale = 36.0;
        int elementWidth = maxX - x2;
        int elementHeight = maxY - y2;
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.isMouseOver() ? ModTextures.BUTTON_LARGE_PRESSED : ModTextures.BUTTON_LARGE);
        draw.drawTexture(x2, y2, 1.46 * (double)elementWidth / 2.0, 1.46 * (double)elementHeight / 2.0, elementWidth / 2, elementHeight / 2);
        draw.drawTexture(x2, y2 + elementHeight / 2, 0.0, 256.0 - 1.46 * (double)elementHeight / 2.0, 1.46 * (double)elementWidth / 2.0, 1.46 * (double)elementHeight / 2.0, elementWidth / 2, elementHeight / 2);
        draw.drawTexture(x2 + elementWidth / 2, y2, 256.0 - 1.46 * (double)elementWidth / 2.0, 0.0, 1.46 * (double)elementWidth / 2.0, 1.46 * (double)elementHeight / 2.0, elementWidth / 2, elementHeight / 2);
        draw.drawTexture(x2 + elementWidth / 2, y2 + elementHeight / 2, 256.0 - 1.46 * (double)elementWidth / 2.0, 256.0 - 1.46 * (double)elementHeight / 2.0, 1.46 * (double)elementWidth / 2.0, 1.46 * (double)elementHeight / 2.0, elementWidth / 2, elementHeight / 2);
        if (this.category.getResourceLocation() != null) {
            GlStateManager.enableAlpha();
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.category.getResourceLocation());
            draw.drawTexture(x2 + 2, y2 + 1, 256.0, 256.0, maxY - y2 - 4, maxY - y2 - 4);
        }
        List<String> list = draw.listFormattedStringToWidth(this.category.getTitle(), maxX - x2 - 64 + 25, 2);
        int posY = list.size() * -5 + 5;
        for (String line : list) {
            draw.drawString(LabyModCore.getMinecraft().getFontRenderer(), line, x2 + maxY - y2 + 2, y2 + 7 + posY, this.isMouseOver() ? 0xFFFFA0 : 0xE0E0E0);
            posY += 10;
        }
    }

    @Override
    public int getEntryHeight() {
        return 22;
    }

    @Override
    public void drawDescription(int x2, int y2, int screenWidth) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver()) {
            this.callback.clicked(this.category);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void unfocus(int mouseX, int mouseY, int mouseButton) {
    }

    public static interface ClickedCallback {
        public void clicked(SettingsCategory var1);
    }
}

