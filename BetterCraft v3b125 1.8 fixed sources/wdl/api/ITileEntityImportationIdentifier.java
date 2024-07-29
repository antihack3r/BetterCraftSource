/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import wdl.api.IWDLMod;

public interface ITileEntityImportationIdentifier
extends IWDLMod {
    public boolean shouldImportTileEntity(String var1, BlockPos var2, Block var3, NBTTagCompound var4, Chunk var5);
}

