/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.elements.basement;

import java.util.List;
import net.labymod.addons.resourcepacks24.gui.elements.basement.DraggableElement;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class PackElement
extends DraggableElement {
    public static final ResourceLocation DEFAULT_ICON = new ResourceLocation("resourcepacks24/textures/pack.png");

    public PackElement(boolean deletable) {
        super(deletable);
    }

    public abstract ResourceLocation getIcon();

    public abstract String getDisplayName();

    public abstract String getDescription();

    @Override
    protected String getElementName() {
        return this.getDisplayName();
    }

    @Override
    public boolean draw(double x2, double y2, double width, double height, int mouseX, int mouseY) {
        boolean mouseOver = super.draw(x2, y2, width, height, mouseX, mouseY);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        if (mouseOver) {
            DrawUtils.drawRect(x2 - 1.0, y2 - 1.0, x2 + width + 1.0, y2 + height + 1.0, ModColor.toRGB(100, 100, 100, 35));
        }
        ResourceLocation resourceLocation = this.getIcon();
        Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation == null ? DEFAULT_ICON : resourceLocation);
        draw.drawTexture(x2, y2, 255.0, 255.0, height, height);
        if (mouseOver) {
            DrawUtils.drawRect(x2, y2, x2 + height, y2 + height, ModColor.toRGB(255, 255, 255, 60));
        }
        String title = draw.trimStringToWidth(this.getDisplayName(), (int)(width - height - 4.0));
        draw.drawString(title, x2 + height + 2.0, y2 + 1.0);
        if (height > 14.0) {
            List<String> list = draw.listFormattedStringToWidth(this.getDescription(), (int)((width - height - 4.0) / 0.8), (int)(height / 8.0 - 1.0));
            int listY = 0;
            for (String line : list) {
                draw.drawString(String.valueOf(ModColor.cl('7')) + line, x2 + height + 2.0, y2 + 10.0 + (double)listY, 0.7);
                listY += 7;
            }
        }
        return mouseOver;
    }
}

