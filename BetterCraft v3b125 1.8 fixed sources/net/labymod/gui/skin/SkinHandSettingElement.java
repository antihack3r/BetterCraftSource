/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.skin;

import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.skin.SkinCustomizationSettingElement;
import net.labymod.utils.Consumer;

public class SkinHandSettingElement
extends SkinCustomizationSettingElement {
    public SkinHandSettingElement(String displayString, String iconName) {
        super(displayString, iconName);
        this.initCheckBox();
        this.checkBox.setUpdateListener(new Consumer<CheckBox.EnumCheckBoxValue>(){

            @Override
            public void accept(CheckBox.EnumCheckBoxValue accepted) {
                LabyModCore.getMinecraft().setUseLeftHand(accepted == CheckBox.EnumCheckBoxValue.ENABLED);
            }
        });
    }

    @Override
    protected CheckBox.EnumCheckBoxValue loadValue() {
        return LabyModCore.getMinecraft().isUsingLeftHand() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED;
    }
}

