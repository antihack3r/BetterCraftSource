/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import wdl.api.IWDLMod;

public interface ITileEntityEditor
extends IWDLMod {
    public boolean shouldEdit(BlockPos var1, NBTTagCompound var2, TileEntityCreationMode var3);

    public void editTileEntity(BlockPos var1, NBTTagCompound var2, TileEntityCreationMode var3);

    public static enum TileEntityCreationMode {
        IMPORTED,
        EXISTING,
        NEW;

    }
}

