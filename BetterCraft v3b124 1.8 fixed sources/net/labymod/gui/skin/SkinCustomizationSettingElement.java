/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.skin;

import java.util.ArrayList;
import java.util.List;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.skin.SkinLayerSettingElement;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class SkinCustomizationSettingElement {
    protected String displayString;
    protected CheckBox checkBox;
    protected ResourceLocation iconResource;
    private List<SkinLayerSettingElement> subSettingElements = new ArrayList<SkinLayerSettingElement>();

    public SkinCustomizationSettingElement(String displayString, String iconName) {
        this.displayString = displayString;
        this.iconResource = new ResourceLocation("labymod/textures/settings/skin/" + iconName + ".png");
    }

    protected void initCheckBox() {
        this.checkBox = new CheckBox("", this.loadValue(), null, 0, 0, 15, 15);
    }

    protected abstract CheckBox.EnumCheckBoxValue loadValue();

    public void draw(boolean subElement, double x2, double y2, double elementWidth, double elementHeight, int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        double textureSize = 292.0;
        double textureScale = 36.0;
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_LARGE_DISABLED);
        draw.drawTexture(x2, y2, 1.46 * elementWidth / 2.0, 1.46 * elementHeight / 2.0, elementWidth / 2.0, elementHeight / 2.0);
        draw.drawTexture(x2, y2 + elementHeight / 2.0, 0.0, 256.0 - 1.46 * elementHeight / 2.0, 1.46 * elementWidth / 2.0, 1.46 * elementHeight / 2.0, elementWidth / 2.0, elementHeight / 2.0);
        draw.drawTexture(x2 + elementWidth / 2.0, y2, 256.0 - 1.46 * elementWidth / 2.0, 0.0, 1.46 * elementWidth / 2.0, 1.46 * elementHeight / 2.0, elementWidth / 2.0, elementHeight / 2.0);
        draw.drawTexture(x2 + elementWidth / 2.0, y2 + elementHeight / 2.0, 256.0 - 1.46 * elementWidth / 2.0, 256.0 - 1.46 * elementHeight / 2.0, 1.46 * elementWidth / 2.0, 1.46 * elementHeight / 2.0, elementWidth / 2.0, elementHeight / 2.0);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.iconResource);
        draw.drawTexture(x2 + elementHeight / 4.0, y2 + elementHeight / 4.0, 256.0, 256.0, elementHeight / 2.0, elementHeight / 2.0);
        draw.drawString(draw.trimStringToWidth(this.displayString, (int)(elementWidth - (double)this.checkBox.getWidth() - 10.0 - elementHeight)), x2 + elementHeight, y2 + elementHeight / 2.0 - 4.0);
        this.checkBox.setX((int)(x2 + elementWidth - (double)this.checkBox.getWidth() - 5.0));
        this.checkBox.setY((int)(y2 + elementHeight / 2.0 - (double)(this.checkBox.getHeight() / 2)));
        this.checkBox.drawCheckbox(mouseX, mouseY);
    }

    public void addSubSetting(SkinLayerSettingElement subSetting) {
        this.subSettingElements.add(subSetting);
        subSetting.getCheckBox().setParentCheckBox(this.checkBox);
        this.checkBox.getChildCheckBoxes().add(subSetting.getCheckBox());
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    public List<SkinLayerSettingElement> getSubSettingElements() {
        return this.subSettingElements;
    }
}

