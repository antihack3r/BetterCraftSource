/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import wdl.api.IWDLMod;

public interface IBlockEventListener
extends IWDLMod {
    public void onBlockEvent(WorldClient var1, BlockPos var2, Block var3, int var4, int var5);
}

