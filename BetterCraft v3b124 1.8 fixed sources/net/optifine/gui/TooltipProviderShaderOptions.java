/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.gui.TooltipProviderOptions;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.gui.GuiButtonShaderOption;
import net.optifine.util.StrUtils;

public class TooltipProviderShaderOptions
extends TooltipProviderOptions {
    @Override
    public String[] getTooltipLines(GuiButton btn, int width) {
        if (!(btn instanceof GuiButtonShaderOption)) {
            return null;
        }
        GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)btn;
        ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
        String[] astring = this.makeTooltipLines(shaderoption, width);
        return astring;
    }

    private String[] makeTooltipLines(ShaderOption so2, int width) {
        String s2 = so2.getNameText();
        String s1 = Config.normalize(so2.getDescriptionText()).trim();
        String[] astring = this.splitDescription(s1);
        GameSettings gamesettings = Config.getGameSettings();
        String s22 = null;
        if (!s2.equals(so2.getName()) && gamesettings.advancedItemTooltips) {
            s22 = "\u00a78" + Lang.get("of.general.id") + ": " + so2.getName();
        }
        String s3 = null;
        if (so2.getPaths() != null && gamesettings.advancedItemTooltips) {
            s3 = "\u00a78" + Lang.get("of.general.from") + ": " + Config.arrayToString(so2.getPaths());
        }
        String s4 = null;
        if (so2.getValueDefault() != null && gamesettings.advancedItemTooltips) {
            String s5 = so2.isEnabled() ? so2.getValueText(so2.getValueDefault()) : Lang.get("of.general.ambiguous");
            s4 = "\u00a78" + Lang.getDefault() + ": " + s5;
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add(s2);
        list.addAll(Arrays.asList(astring));
        if (s22 != null) {
            list.add(s22);
        }
        if (s3 != null) {
            list.add(s3);
        }
        if (s4 != null) {
            list.add(s4);
        }
        String[] astring1 = this.makeTooltipLines(width, list);
        return astring1;
    }

    private String[] splitDescription(String desc) {
        if (desc.length() <= 0) {
            return new String[0];
        }
        desc = StrUtils.removePrefix(desc, "//");
        String[] astring = desc.split("\\. ");
        int i2 = 0;
        while (i2 < astring.length) {
            astring[i2] = "- " + astring[i2].trim();
            astring[i2] = StrUtils.removeSuffix(astring[i2], ".");
            ++i2;
        }
        return astring;
    }

    private String[] makeTooltipLines(int width, List<String> args) {
        FontRenderer fontrenderer = Config.getMinecraft().fontRendererObj;
        ArrayList<String> list = new ArrayList<String>();
        int i2 = 0;
        while (i2 < args.size()) {
            String s2 = args.get(i2);
            if (s2 != null && s2.length() > 0) {
                for (String s1 : fontrenderer.listFormattedStringToWidth(s2, width)) {
                    list.add(s1);
                }
            }
            ++i2;
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}

