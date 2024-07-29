/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.client.multiplayer.WorldClient;
import wdl.api.IWDLMod;

public interface IWorldLoadListener
extends IWDLMod {
    public void onWorldLoad(WorldClient var1, boolean var2);
}

