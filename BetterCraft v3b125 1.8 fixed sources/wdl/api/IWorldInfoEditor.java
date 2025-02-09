/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import wdl.api.IWDLMod;

public interface IWorldInfoEditor
extends IWDLMod {
    public void editWorldInfo(WorldClient var1, WorldInfo var2, SaveHandler var3, NBTTagCompound var4);
}

