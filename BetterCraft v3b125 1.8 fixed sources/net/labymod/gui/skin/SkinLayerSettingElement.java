/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.skin;

import java.util.Set;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.skin.GuiSkinCustomization;
import net.labymod.gui.skin.SkinCustomizationSettingElement;
import net.labymod.utils.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class SkinLayerSettingElement
extends SkinCustomizationSettingElement {
    private EnumPlayerModelParts[] modelParts;

    public SkinLayerSettingElement(final GuiSkinCustomization skinCustomization, String displayString, String iconName, final EnumPlayerModelParts ... modelParts) {
        super(modelParts.length == 1 ? I18n.format("options.modelPart." + modelParts[0].getPartName(), new Object[0]) : displayString, iconName);
        this.modelParts = modelParts;
        this.initCheckBox();
        this.checkBox.setUpdateListener(new Consumer<CheckBox.EnumCheckBoxValue>(){

            @Override
            public void accept(CheckBox.EnumCheckBoxValue accepted) {
                EnumPlayerModelParts[] enumPlayerModelPartsArray = modelParts;
                int n2 = modelParts.length;
                int n3 = 0;
                while (n3 < n2) {
                    EnumPlayerModelParts part = enumPlayerModelPartsArray[n3];
                    if (accepted != CheckBox.EnumCheckBoxValue.INDETERMINATE) {
                        skinCustomization.updatePart(part, accepted == CheckBox.EnumCheckBoxValue.ENABLED);
                    }
                    ++n3;
                }
            }
        });
    }

    @Override
    protected CheckBox.EnumCheckBoxValue loadValue() {
        Set<EnumPlayerModelParts> enabledList = Minecraft.getMinecraft().gameSettings.getModelParts();
        if (this.modelParts.length == 1) {
            return enabledList.contains((Object)this.modelParts[0]) ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED;
        }
        boolean allContains = true;
        boolean allContainsNot = true;
        EnumPlayerModelParts[] enumPlayerModelPartsArray = this.modelParts;
        int n2 = this.modelParts.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumPlayerModelParts part = enumPlayerModelPartsArray[n3];
            if (!enabledList.contains((Object)part)) {
                allContains = false;
            } else {
                allContainsNot = false;
            }
            ++n3;
        }
        return allContains ? CheckBox.EnumCheckBoxValue.ENABLED : (allContainsNot ? CheckBox.EnumCheckBoxValue.DISABLED : CheckBox.EnumCheckBoxValue.INDETERMINATE);
    }
}

