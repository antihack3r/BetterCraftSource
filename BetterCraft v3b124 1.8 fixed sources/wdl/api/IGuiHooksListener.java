/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import wdl.api.IWDLMod;

public interface IGuiHooksListener
extends IWDLMod {
    public boolean onBlockGuiClosed(WorldClient var1, BlockPos var2, Container var3);

    public boolean onEntityGuiClosed(WorldClient var1, Entity var2, Container var3);
}

