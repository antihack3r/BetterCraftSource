// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ITileEntityProvider
{
    @Nullable
    TileEntity createNewTileEntity(final World p0, final int p1);
}
