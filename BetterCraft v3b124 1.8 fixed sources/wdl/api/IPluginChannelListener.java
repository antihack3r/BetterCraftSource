/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.client.multiplayer.WorldClient;
import wdl.api.IWDLMod;

public interface IPluginChannelListener
extends IWDLMod {
    public void onPluginChannelPacket(WorldClient var1, String var2, byte[] var3);
}

