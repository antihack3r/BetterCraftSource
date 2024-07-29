/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Lang;
import net.optifine.gui.IOptionControl;
import net.optifine.gui.TooltipProvider;

public class TooltipProviderOptions
implements TooltipProvider {
    @Override
    public Rectangle getTooltipBounds(GuiScreen guiScreen, int x2, int y2) {
        int i2 = GuiScreen.width / 2 - 150;
        int j2 = GuiScreen.height / 6 - 7;
        if (y2 <= j2 + 98) {
            j2 += 105;
        }
        int k2 = i2 + 150 + 150;
        int l2 = j2 + 84 + 10;
        return new Rectangle(i2, j2, k2 - i2, l2 - j2);
    }

    @Override
    public boolean isRenderBorder() {
        return false;
    }

    @Override
    public String[] getTooltipLines(GuiButton btn, int width) {
        if (!(btn instanceof IOptionControl)) {
            return null;
        }
        IOptionControl ioptioncontrol = (IOptionControl)((Object)btn);
        GameSettings.Options gamesettings$options = ioptioncontrol.getOption();
        String[] astring = TooltipProviderOptions.getTooltipLines(gamesettings$options.getEnumString());
        return astring;
    }

    public static String[] getTooltipLines(String key) {
        ArrayList<String> list = new ArrayList<String>();
        int i2 = 0;
        while (i2 < 10) {
            String s2 = String.valueOf(key) + ".tooltip." + (i2 + 1);
            String s1 = Lang.get(s2, null);
            if (s1 == null) break;
            list.add(s1);
            ++i2;
        }
        if (list.size() <= 0) {
            return null;
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}

