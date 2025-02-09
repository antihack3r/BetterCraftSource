/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import wdl.api.IWDLMod;

public interface IPlayerInfoEditor
extends IWDLMod {
    public void editPlayerInfo(EntityPlayerSP var1, SaveHandler var2, NBTTagCompound var3);
}

