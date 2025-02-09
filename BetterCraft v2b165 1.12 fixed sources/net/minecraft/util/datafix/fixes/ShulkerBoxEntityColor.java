// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util.datafix.fixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class ShulkerBoxEntityColor implements IFixableData
{
    @Override
    public int getFixVersion() {
        return 808;
    }
    
    @Override
    public NBTTagCompound fixTagCompound(final NBTTagCompound compound) {
        if ("minecraft:shulker".equals(compound.getString("id")) && !compound.hasKey("Color", 99)) {
            compound.setByte("Color", (byte)10);
        }
        return compound;
    }
}
