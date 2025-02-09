/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.client.gui.GuiScreen;
import wdl.api.IWDLMod;

public interface IWDLModWithGui
extends IWDLMod {
    public String getButtonName();

    public void openGui(GuiScreen var1);
}

