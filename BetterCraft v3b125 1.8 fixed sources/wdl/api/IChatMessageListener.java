/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.client.multiplayer.WorldClient;
import wdl.api.IWDLMod;

public interface IChatMessageListener
extends IWDLMod {
    public void onChat(WorldClient var1, String var2);
}

