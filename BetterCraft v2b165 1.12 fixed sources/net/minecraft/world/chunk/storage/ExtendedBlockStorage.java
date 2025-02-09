// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import optifine.Reflector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.BlockStateContainer;

public class ExtendedBlockStorage
{
    private final int yBase;
    private int blockRefCount;
    private int tickRefCount;
    private final BlockStateContainer data;
    private NibbleArray blocklightArray;
    private NibbleArray skylightArray;
    
    public ExtendedBlockStorage(final int y, final boolean storeSkylight) {
        this.yBase = y;
        this.data = new BlockStateContainer();
        this.blocklightArray = new NibbleArray();
        if (storeSkylight) {
            this.skylightArray = new NibbleArray();
        }
    }
    
    public IBlockState get(final int x, final int y, final int z) {
        return this.data.get(x, y, z);
    }
    
    public void set(final int x, final int y, final int z, IBlockState state) {
        if (Reflector.IExtendedBlockState.isInstance(state)) {
            state = (IBlockState)Reflector.call(state, Reflector.IExtendedBlockState_getClean, new Object[0]);
        }
        final IBlockState iblockstate = this.get(x, y, z);
        final Block block = iblockstate.getBlock();
        final Block block2 = state.getBlock();
        if (block != Blocks.AIR) {
            --this.blockRefCount;
            if (block.getTickRandomly()) {
                --this.tickRefCount;
            }
        }
        if (block2 != Blocks.AIR) {
            ++this.blockRefCount;
            if (block2.getTickRandomly()) {
                ++this.tickRefCount;
            }
        }
        this.data.set(x, y, z, state);
    }
    
    public boolean isEmpty() {
        return this.blockRefCount == 0;
    }
    
    public boolean getNeedsRandomTick() {
        return this.tickRefCount > 0;
    }
    
    public int getYLocation() {
        return this.yBase;
    }
    
    public void setExtSkylightValue(final int x, final int y, final int z, final int value) {
        this.skylightArray.set(x, y, z, value);
    }
    
    public int getExtSkylightValue(final int x, final int y, final int z) {
        return this.skylightArray.get(x, y, z);
    }
    
    public void setExtBlocklightValue(final int x, final int y, final int z, final int value) {
        this.blocklightArray.set(x, y, z, value);
    }
    
    public int getExtBlocklightValue(final int x, final int y, final int z) {
        return this.blocklightArray.get(x, y, z);
    }
    
    public void removeInvalidBlocks() {
        final IBlockState iblockstate = Blocks.AIR.getDefaultState();
        int i = 0;
        int j = 0;
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                for (int i2 = 0; i2 < 16; ++i2) {
                    final IBlockState iblockstate2 = this.data.get(i2, k, l);
                    if (iblockstate2 != iblockstate) {
                        ++i;
                        final Block block = iblockstate2.getBlock();
                        if (block.getTickRandomly()) {
                            ++j;
                        }
                    }
                }
            }
        }
        this.blockRefCount = i;
        this.tickRefCount = j;
    }
    
    public BlockStateContainer getData() {
        return this.data;
    }
    
    public NibbleArray getBlocklightArray() {
        return this.blocklightArray;
    }
    
    public NibbleArray getSkylightArray() {
        return this.skylightArray;
    }
    
    public void setBlocklightArray(final NibbleArray newBlocklightArray) {
        this.blocklightArray = newBlocklightArray;
    }
    
    public void setSkylightArray(final NibbleArray newSkylightArray) {
        this.skylightArray = newSkylightArray;
    }
}
