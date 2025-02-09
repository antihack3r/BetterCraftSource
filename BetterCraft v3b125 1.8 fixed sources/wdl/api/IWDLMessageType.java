/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.util.EnumChatFormatting;

public interface IWDLMessageType {
    public EnumChatFormatting getTitleColor();

    public EnumChatFormatting getTextColor();

    public String getDisplayName();

    public String getDescription();

    public boolean isEnabledByDefault();
}

